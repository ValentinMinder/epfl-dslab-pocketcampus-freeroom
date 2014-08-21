// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Windows;
using PocketCampus.Common.Controls;
using ThinMvvm;
using ThinMvvm.Logging;

namespace PocketCampus.Transport.Controls
{
    /// <summary>
    /// Bi-state expander, with a header and two contents.
    /// </summary>
    [TemplateVisualState( GroupName = "ExpandedStates", Name = "Collapsed" )]
    [TemplateVisualState( GroupName = "ExpandedStates", Name = "Expanded" )]
    public partial class BiStateExpander : ObservableControl
    {
        #region Header DependencyProperty
        /// <summary>
        /// The header.
        /// </summary>
        public object Header
        {
            get { return (object) GetValue( HeaderProperty ); }
            set { SetValue( HeaderProperty, value ); }
        }

        public static readonly DependencyProperty HeaderProperty =
            DependencyProperty.Register( "Header", typeof( object ), typeof( BiStateExpander ), new PropertyMetadata( null ) );
        #endregion

        #region CollapsedContent DependencyProperty
        /// <summary>
        /// The content displayed when the expander is collapsed.
        /// </summary>
        public object CollapsedContent
        {
            get { return (object) GetValue( CollapsedContentProperty ); }
            set { SetValue( CollapsedContentProperty, value ); }
        }

        public static readonly DependencyProperty CollapsedContentProperty =
            DependencyProperty.Register( "CollapsedContent", typeof( object ), typeof( BiStateExpander ), new PropertyMetadata( null ) );
        #endregion

        #region ExpandedContent DependencyProperty
        /// <summary>
        /// The content displayed when the expander is expanded.
        /// </summary>
        public object ExpandedContent
        {
            get { return (object) GetValue( ExpandedContentProperty ); }
            set { SetValue( ExpandedContentProperty, value ); }
        }

        public static readonly DependencyProperty ExpandedContentProperty =
            DependencyProperty.Register( "ExpandedContent", typeof( object ), typeof( BiStateExpander ), new PropertyMetadata( null ) );
        #endregion

        private bool _isExpanded;

        /// <summary>
        /// Gets the command executed to switch between the states.
        /// </summary>
        [LogId( "OpenOrCloseInfo" )]
        public Command SwitchStateCommand
        {
            get { return this.GetCommand( SwitchState ); }
        }


        /// <summary>
        /// Creates a new BiStateExpander.
        /// </summary>
        public BiStateExpander()
        {
            InitializeComponent();

            Messenger.Send( new CommandLoggingRequest( this ) );
        }

        /// <summary>
        /// Switches between the states.
        /// </summary>
        private void SwitchState()
        {
            _isExpanded = !_isExpanded;
            string newStateName = _isExpanded ? "Expanded" : "Collapsed";
            VisualStateManager.GoToState( this, newStateName, true );
        }
    }
}