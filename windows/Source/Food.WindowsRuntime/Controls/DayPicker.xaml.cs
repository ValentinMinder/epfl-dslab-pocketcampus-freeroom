// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using ThinMvvm;
using ThinMvvm.Logging;
using Windows.UI.Xaml;

namespace PocketCampus.Food.Controls
{
    public sealed partial class DayPicker : ICommandOwner
    {
        #region Value
        public DateTime Value
        {
            get { return (DateTime) GetValue( ValueProperty ); }
            set { SetValue( ValueProperty, value ); }
        }

        public static readonly DependencyProperty ValueProperty =
            DependencyProperty.Register( "Value", typeof( DateTime ), typeof( DayPicker ), new PropertyMetadata( DateTime.Now ) );
        #endregion

        #region TextStyle
        public Style TextStyle
        {
            get { return (Style) GetValue( TextStyleProperty ); }
            set { SetValue( TextStyleProperty, value ); }
        }

        public static readonly DependencyProperty TextStyleProperty =
            DependencyProperty.Register( "TextStyle", typeof( Style ), typeof( DayPicker ), new PropertyMetadata( null ) );
        #endregion


        [LogId( "PreviousDay" )]
        public Command PreviousCommand
        {
            get { return this.GetCommand( () => Value = Value.AddDays( -1 ) ); }
        }

        [LogId( "NextDay" )]
        public Command NextCommand
        {
            get { return this.GetCommand( () => Value = Value.AddDays( 1 ) ); }
        }


        public DayPicker()
        {
            InitializeComponent();
            Root.DataContext = this;

            Messenger.Send( new CommandLoggingRequest( this ) );
        }
    }
}