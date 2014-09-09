using System;
using ThinMvvm;
using ThinMvvm.Logging;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;

namespace PocketCampus.IsAcademia.Controls
{
    public sealed partial class WeekPicker : UserControl, ICommandOwner
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
            Root.DataContext = this;

            Messenger.Send( new CommandLoggingRequest( this ) );
        }

        /// <summary>
        /// Selects the next week.
        /// </summary>
        private void SelectNext()
        {
            Value = Value.AddDays( 7 );
        }

        /// <summary>
        /// Selects the previous week.
        /// </summary>
        private void SelectPrevious()
        {
            Value = Value.AddDays( -7 );
        }
    }
}