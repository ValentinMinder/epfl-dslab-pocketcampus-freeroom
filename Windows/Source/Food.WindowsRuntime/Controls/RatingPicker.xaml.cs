// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using Windows.UI.Xaml;

namespace PocketCampus.Food.Controls
{
    public sealed partial class RatingPicker
    {
        #region Value DependencyProperty
        public UserRating Value
        {
            get { return (UserRating) GetValue( ValueProperty ); }
            set { SetValue( ValueProperty, value ); }
        }

        public static readonly DependencyProperty ValueProperty =
            DependencyProperty.Register( "Value", typeof( UserRating ), typeof( RatingPicker ), new PropertyMetadata( UserRating.Neutral ) );
        #endregion

        // HACK: Declaring these in XAML makes them ints :-/
        public UserRating[] AvailableRatings
        {
            get { return new[] { UserRating.Bad, UserRating.Neutral, UserRating.Good }; }
        }


        public RatingPicker()
        {
            InitializeComponent();
            Root.DataContext = this;
        }
    }
}