// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Diagnostics;
using System.Linq.Expressions;

namespace PocketCampus.Mvvm
{
    /// <summary>
    /// The base class for Commands.
    /// </summary>
    /// <remarks>
    /// ICommand is implemented partially to let implementers implement it explicitly
    /// so that the parameters can be ignored or of the correct type.
    /// </remarks>
    public abstract class CommandBase
    {
        /// <summary>
        /// Creates a new CommandBase with the specified predicate.
        /// </summary>
        /// <param name="canExecute">The predicate indicating whether the command can be executed, or null to always execute it.</param>
        public CommandBase( Expression canExecute )
        {
            if ( canExecute == null )
            {
                return;
            }

            foreach ( var obsProps in ObservablePropertyVisitor.GetObservablePropertyAccesses( canExecute ) )
            {
                obsProps.Item1.PropertyChanged += ( s, e ) =>
                {
                    if ( e.PropertyName == obsProps.Item2 )
                    {
                        OnCanExecuteChanged();
                    }
                };
            }
        }

        #region Partial ICommand implementation
        /// <summary>
        /// Occurs when changes occur that affect whether or not the command should execute.
        /// </summary>
        public event EventHandler CanExecuteChanged;
        /// <summary>
        /// Fires the CanExecuteChanged event.
        /// </summary>
        public void OnCanExecuteChanged()
        {
            var evt = CanExecuteChanged;
            if ( evt != null )
            {
                evt( this, EventArgs.Empty );
            }
        }
        #endregion

        /// <summary>
        /// Occurs when the command is executed.
        /// </summary>
        internal event EventHandler Executed;
        /// <summary>
        /// Fires the Executed event.
        /// </summary>
        protected void OnExecuted()
        {
            var evt = Executed;
            if ( evt != null )
            {
                evt( this, EventArgs.Empty );
            }
        }

        /// <summary>
        /// Visits expressions and builds sequences of (object, property name) tuples for observable properties found in the expressions.
        /// </summary>
        private sealed class ObservablePropertyVisitor : ExpressionVisitor
        {
            private List<Tuple<INotifyPropertyChanged, string>> _propertyAccesses;

            private ObservablePropertyVisitor( Expression expr )
            {
                _propertyAccesses = new List<Tuple<INotifyPropertyChanged, string>>();
                Visit( expr );
            }

            public static IEnumerable<Tuple<INotifyPropertyChanged, string>> GetObservablePropertyAccesses( Expression expr )
            {
                return new ObservablePropertyVisitor( expr )._propertyAccesses;
            }

            protected override Expression VisitMember( MemberExpression node )
            {
                var owner = GetPropertyOwner( node );
                var ownerNotif = owner as INotifyPropertyChanged;
                if ( ownerNotif != null )
                {
                    _propertyAccesses.Add( Tuple.Create( ownerNotif, node.Member.Name ) );
                }
                return base.VisitMember( node );
            }

            private static object GetPropertyOwner( MemberExpression propertyExpr )
            {
                var constExpr = propertyExpr.Expression as ConstantExpression;
                if ( constExpr == null )
                {
                    Debug.WriteLine( "WARNING: Expression {0} contains a property not on 'this'. Changes will not be tracked.", propertyExpr );
                    return null;
                }

                return constExpr.Value;
            }
        }
    }
}