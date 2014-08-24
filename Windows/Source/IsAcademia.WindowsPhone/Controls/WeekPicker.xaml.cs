// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Linq;
using System.Windows;
using PocketCampus.Common.Controls;
using ThinMvvm;
using ThinMvvm.Logging;

namespace PocketCampus.IsAcademia.Controls
{
    /// <summary>
    /// Picks weeks.
    /// </summary>
    public partial class WeekPicker : ObservableControl
    {
        private const string DifferentMonthFormat = "{0:M} - {1:M}";

        // There's no standard way to display a localized version of "December 2 - 8", since order can vary (e.g. French is "2 - 8 décembre")
        // Simple workaround: Find the number.
        // The percent is necessary for 'd' to be interpreted as 'day between 1 and 31' instead of 'standard date format'.
        private static readonly string SameMonthFormat = DateTime.Now.ToString( "M" ).Split( ' ' )[0].Any( char.IsDigit ) ?
                                                         "{0:%d} - {1:M}"
                                                       : "{0:M} - {1:%d}";

        #region Value DependencyProperty
        /// <summary>
        /// The week.
        /// </summary>
        public DateTime Value
        {
            get { return (DateTime) GetValue( ValueProperty ); }
            set { SetValue( ValueProperty, value ); }
        }

        public static readonly DependencyProperty ValueProperty =
            DependencyProperty.Register( "Value", typeof( DateTime ), typeof( WeekPicker ), new PropertyMetadata( OnSelectedWeekPropertyChanged ) );

        private static void OnSelectedWeekPropertyChanged( DependencyObject obj, DependencyPropertyChangedEventArgs args )
        {
            var picker = (WeekPicker) obj;
            picker.FixDate();
            picker.SetDisplayDate();
        }
        #endregion

        #region TextStyle DependencyProperty
        /// <summary>
        /// The style for text blocks.
        /// </summary>
        public Style TextStyle
        {
            get { return (Style) GetValue( TextStyleProperty ); }
            set { SetValue( TextStyleProperty, value ); }
        }

        public static readonly DependencyProperty TextStyleProperty =
            DependencyProperty.Register( "TextStyle", typeof( Style ), typeof( WeekPicker ), new PropertyMetadata( null ) );
        #endregion

        private string _displayDate;

        /// <summary>
        /// A human-readable version of the current date.
        /// </summary>
        public string DisplayDate
        {
            get { return _displayDate; }
            set { SetProperty( ref _displayDate, value ); }
        }

        /// <summary>
        /// Gets the command that selects the next week.
        /// </summary>
        [LogId( "NextWeek" )]
        public Command NextCommand
        {
            get { return this.GetCommand( SelectNext ); }
        }

        /// <summary>
        /// Gets the command that selects the previous week.
        /// </summary>
        [LogId( "PreviousWeek" )]
        public Command PreviousCommand
        {
            get { return this.GetCommand( SelectPrevious ); }
        }


        /// <summary>
        /// Creates a new WeekPicker control.
        /// </summary>
        public WeekPicker()
        {
            InitializeComponent();
            SetDisplayDate();
            LayoutRoot.DataContext = this;

            Messenger.Send( new CommandLoggingRequest( this ) );
        }

        /// <summary>
        /// Selects the next week.
        /// </summary>
        private void SelectNext()
        {
            Value = Value.AddDays( 7 );
            SetDisplayDate();
        }

        /// <summary>
        /// Selects the previous week.
        /// </summary>
        private void SelectPrevious()
        {
            Value = Value.AddDays( -7 );
            SetDisplayDate();
        }

        /// <summary>
        /// Ensures the selected date is the start of a week.
        /// </summary>
        private void FixDate()
        {
            if ( Value.DayOfWeek != DayOfWeek.Monday )
            {
                int daysToRemove = Value.DayOfWeek - DayOfWeek.Monday;
                Value = Value.Subtract( TimeSpan.FromDays( daysToRemove ) );
            }
        }

        /// <summary>
        /// Sets the display date.
        /// </summary>
        private void SetDisplayDate()
        {
            var end = Value.AddDays( 6 );
            string format = Value.Month == end.Month ? SameMonthFormat : DifferentMonthFormat;
            DisplayDate = string.Format( format, Value, end );
        }
    }
}