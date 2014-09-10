using PocketCampus.Food.Models;
using ThinMvvm;
using ThinMvvm.Logging;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;

namespace PocketCampus.Food.Controls
{
    public sealed partial class MealTimePicker : UserControl, ICommandOwner
    {
        #region Value DependencyProperty
        public MealTime Value
        {
            get { return (MealTime) GetValue( ValueProperty ); }
            set { SetValue( ValueProperty, value ); }
        }

        public static readonly DependencyProperty ValueProperty =
            DependencyProperty.Register( "Value", typeof( MealTime ), typeof( MealTimePicker ), new PropertyMetadata( MealTime.Lunch ) );
        #endregion

        #region TextStyle DependencyProperty
        public Style TextStyle
        {
            get { return (Style) GetValue( TextStyleProperty ); }
            set { SetValue( TextStyleProperty, value ); }
        }

        public static readonly DependencyProperty TextStyleProperty =
            DependencyProperty.Register( "TextStyle", typeof( Style ), typeof( MealTimePicker ), new PropertyMetadata( null ) );
        #endregion


        /// <summary>
        /// Gets the command executed to set the meal to Lunch.
        /// </summary>
        [LogId( "ViewLunch" )]
        public Command SetLunchCommand
        {
            get { return this.GetCommand( () => Value = MealTime.Lunch ); }
        }

        /// <summary>
        /// Gets the command executed to set the meal to Dinner.
        /// </summary>
        [LogId( "ViewDinner" )]
        public Command SetDinnerCommand
        {
            get { return this.GetCommand( () => Value = MealTime.Dinner ); }
        }


        public MealTimePicker()
        {
            InitializeComponent();
            Root.DataContext = this;

            Messenger.Send( new CommandLoggingRequest( this ) );
        }
    }
}