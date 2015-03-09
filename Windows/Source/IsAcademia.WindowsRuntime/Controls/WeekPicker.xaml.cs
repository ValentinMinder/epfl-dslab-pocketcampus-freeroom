// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using ThinMvvm;
using ThinMvvm.Logging;
using Windows.UI.Xaml;

namespace PocketCampus.IsAcademia.Controls
{
    public sealed partial class WeekPicker : ICommandOwner
    {
        #region Value
        public DateTime Value
        {
            get { return (DateTime) GetValue( ValueProperty ); }
            set { SetValue( ValueProperty, value ); }
        }

        public static readonly DependencyProperty ValueProperty =
            DependencyProperty.Register( "Value", typeof( DateTime ), typeof( WeekPicker ), new PropertyMetadata( default( DateTime ), OnValueChanged ) );

        private static void OnValueChanged( DependencyObject obj, DependencyPropertyChangedEventArgs args )
        {
            var picker = (WeekPicker) obj;
            var value = (DateTime) args.NewValue;

            if ( value.DayOfWeek != DayOfWeek.Monday )
            {
                int daysToRemove = value.DayOfWeek - DayOfWeek.Monday;
                picker.Value = value.AddDays( -daysToRemove );
            }
        }
        #endregion

        #region TextStyle
        public Style TextStyle
        {
            get { return (Style) GetValue( TextStyleProperty ); }
            set { SetValue( TextStyleProperty, value ); }
        }

        public static readonly DependencyProperty TextStyleProperty =
            DependencyProperty.Register( "TextStyle", typeof( Style ), typeof( WeekPicker ), new PropertyMetadata( null ) );
        #endregion


        [LogId( "NextWeek" )]
        public Command NextCommand
        {
            get { return this.GetCommand( () => Value = Value.AddDays( 7 ) ); }
        }

        [LogId( "PreviousWeek" )]
        public Command PreviousCommand
        {
            get { return this.GetCommand( () => Value = Value.AddDays( -7 ) ); }
        }


        public WeekPicker()
        {
            InitializeComponent();
            Root.DataContext = this;

            Messenger.Send( new CommandLoggingRequest( this ) );
        }
    }
}