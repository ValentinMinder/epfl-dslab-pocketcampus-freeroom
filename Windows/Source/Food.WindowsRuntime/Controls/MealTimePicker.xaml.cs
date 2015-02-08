// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using PocketCampus.Food.Models;
using ThinMvvm;
using ThinMvvm.Logging;
using Windows.UI.Xaml;

namespace PocketCampus.Food.Controls
{
    public sealed partial class MealTimePicker : ICommandOwner
    {
        #region Value
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

        // HACK: Declaring these in XAML makes them ints :-/
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