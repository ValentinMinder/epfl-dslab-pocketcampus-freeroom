// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Windows;
using System.Windows.Controls;
using PocketCampus.Common.Controls;
using PocketCampus.Food.Models;
using ThinMvvm;
using ThinMvvm.Logging;

namespace PocketCampus.Food.Controls
{
    /// <summary>
    /// Picks meal times.
    /// </summary>
    [TemplateVisualState( GroupName = "ValueStates", Name = "Lunch" )]
    [TemplateVisualState( GroupName = "ValueStates", Name = "Dinner" )]
    public partial class MealTimePicker : ObservableControl
    {
        #region Value DependencyProperty
        /// <summary>
        /// The selected meal time.
        /// </summary>
        public MealTime Value
        {
            get { return (MealTime) GetValue( ValueProperty ); }
            set { SetValue( ValueProperty, value ); }
        }

        public static readonly DependencyProperty ValueProperty =
            DependencyProperty.Register( "Value", typeof( MealTime ), typeof( MealTimePicker ), new PropertyMetadata( OnValuePropertyChanged ) );

        private static void OnValuePropertyChanged( DependencyObject obj, DependencyPropertyChangedEventArgs args )
        {
            VisualStateManager.GoToState( (Control) obj, args.NewValue.ToString(), true );
        }
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


        /// <summary>
        /// Creates a new MealPicker.
        /// </summary>
        public MealTimePicker()
        {
            InitializeComponent();
            LayoutRoot.DataContext = this;

            Messenger.Send( new CommandLoggingRequest( this ) );
        }
    }
}