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
            DependencyProperty.Register( "Value", typeof( MealTime ), typeof( MealTimePicker ), new PropertyMetadata( MealTime.Lunch, OnValueChanged ) );

        private static void OnValueChanged( DependencyObject obj, DependencyPropertyChangedEventArgs args )
        {
            var time = args.NewValue.ToString();
            Messenger.Send( new EventLogRequest( "View" + time, null ) );
        }
        #endregion

        // HACK when defined in XAML they're ints :(
        public MealTime[] AvailableTimes
        {
            get { return new[] { MealTime.Lunch, MealTime.Dinner }; }
        }

        public MealTimePicker()
        {
            InitializeComponent();
            Root.DataContext = this;
        }
    }
}