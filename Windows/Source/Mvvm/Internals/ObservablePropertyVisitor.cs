// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq.Expressions;
using System.Reflection;

namespace PocketCampus.Mvvm.Internals
{
    /// <summary>
    /// Visits expressions and builds sequences of (object, property name) tuples for observable properties found in the expressions.
    /// </summary>
    internal sealed class ObservablePropertyVisitor : ExpressionVisitor
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
            var ownerAndName = GetPropertyOwnerAndName( node );
            if ( ownerAndName != null )
            {
                var ownerNotif = ownerAndName.Item1 as INotifyPropertyChanged;
                if ( ownerNotif != null )
                {
                    _propertyAccesses.Add( Tuple.Create( ownerNotif, ownerAndName.Item2 ) );
                }
            }

            return base.VisitMember( node );
        }

        private static Tuple<object, string> GetPropertyOwnerAndName( MemberExpression propertyExpr )
        {
            string name = propertyExpr.Member.Name;

            var constExpr = propertyExpr.Expression as ConstantExpression;
            if ( constExpr != null )
            {
                return Tuple.Create( constExpr.Value, name );
            }

            // Magic to get the owner/name of a property access not on 'this'
            var memberExpr = propertyExpr.Expression as MemberExpression;
            if ( memberExpr != null )
            {
                var memberConstExpr = memberExpr.Expression as ConstantExpression;
                if ( memberConstExpr != null )
                {
                    var field = memberExpr.Member as FieldInfo;
                    if ( field != null )
                    {
                        return Tuple.Create( field.GetValue( memberConstExpr.Value ), name );
                    }
                }
            }

            return null;
        }
    }
}