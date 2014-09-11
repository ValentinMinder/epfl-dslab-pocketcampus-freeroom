using System;
using ThinMvvm;
using ThinMvvm.Logging;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;

namespace PocketCampus.Food.Controls
{
    public sealed partial class DayPicker : UserControl, ICommandOwner
    {
        #region Value DependencyProperty
        public DateTime Value
        {
            get { return (DateTime) GetValue( ValueProperty ); }
            set { SetValue( ValueProperty, value ); }
        }

        public static readonly DependencyProperty ValueProperty =
            DependencyProperty.Register( "Value", typeof( DateTime ), typeof( DayPicker ), new PropertyMetadata( DateTime.Now ) );
        #endregion

        #region TextStyle DependencyProperty
        public Style TextStyle
        {
            get { return (Style) GetValue( TextStyleProperty ); }
            set { SetValue( TextStyleProperty, value ); }
        }

        public static readonly DependencyProperty TextStyleProperty =
            DependencyProperty.Register( "TextStyle", typeof( Style ), typeof( DayPicker ), new PropertyMetadata( null ) );
        #endregion

        /// <summary>
        /// Gets the command executed to select the previous day.
        /// </summary>
        [LogId( "PreviousDay" )]
        public Command PreviousCommand
        {
            get { return this.GetCommand( () => Value = Value.AddDays( -1 ) ); }
        }

        /// <summary>
        /// Gets the command executed to select the next day.
        /// </summary>
        [LogId( "NextDay" )]
        public Command NextCommand
        {
            get { return this.GetCommand( () => Value = Value.AddDays( 1 ) ); }
        }


        /// <summary>
        /// Creates a new DayPicker.
        /// </summary>
        public DayPicker()
        {
            InitializeComponent();
            Root.DataContext = this;

            Messenger.Send( new CommandLoggingRequest( this ) );
        }
    }
}