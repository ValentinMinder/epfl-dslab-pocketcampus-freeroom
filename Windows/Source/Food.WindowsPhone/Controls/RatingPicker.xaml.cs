// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Windows;
using PocketCampus.Common.Controls;

namespace PocketCampus.Food.Controls
{
    /// <summary>
    /// Picks a rating.
    /// </summary>
    public partial class RatingPicker : ObservableControl
    {
        #region Value DependencyProperty
        /// <summary>
        /// The selected rating.
        /// </summary>
        public UserRating Value
        {
            get { return (UserRating) GetValue( ValueProperty ); }
            set { SetValue( ValueProperty, value ); }
        }

        public static readonly DependencyProperty ValueProperty =
            DependencyProperty.Register( "Value", typeof( UserRating ), typeof( RatingPicker ), new PropertyMetadata( OnValueChanged ) );

        private static void OnValueChanged( DependencyObject obj, DependencyPropertyChangedEventArgs args )
        {
            var picker = (RatingPicker) obj;
            picker.OnPropertyChanged( "IsBad" );
            picker.OnPropertyChanged( "IsNeutral" );
            picker.OnPropertyChanged( "IsGood" );
        }
        #endregion


        /// <summary>
        /// Gets or sets a value indicating whether the selected rating is "Bad".
        /// </summary>
        public bool IsBad
        {
            get { return Value == UserRating.Bad; }
            set
            {
                if ( value )
                {
                    Value = UserRating.Bad;
                }
            }
        }

        /// <summary>
        /// Gets or sets a value indicating whether the selected rating is "Neutral".
        /// </summary>
        public bool IsNeutral
        {
            get { return Value == UserRating.Neutral; }
            set
            {
                if ( value )
                {
                    Value = UserRating.Neutral;
                }
            }
        }

        /// <summary>
        /// Gets or sets a value indicating whether the selected rating is "Good".
        /// </summary>
        public bool IsGood
        {
            get { return Value == UserRating.Good; }
            set
            {
                if ( value )
                {
                    Value = UserRating.Good;
                }
            }
        }


        /// <summary>
        /// Creates a new RatingPicker.
        /// </summary>
        public RatingPicker()
        {
            InitializeComponent();
            LayoutRoot.DataContext = this;
        }
    }
}