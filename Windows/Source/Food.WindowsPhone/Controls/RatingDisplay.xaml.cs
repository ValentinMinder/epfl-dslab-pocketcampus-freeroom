// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Windows;
using System.Windows.Controls;
using PocketCampus.Food.Models;

namespace PocketCampus.Food.Controls
{
    /// <summary>
    /// Displays a rating, in the form of a bar and a number of votes.
    /// </summary>
    public partial class RatingDisplay : UserControl
    {
        #region Value DependencyProperty
        /// <summary>
        /// The rating to display.
        /// </summary>
        public Rating Rating
        {
            get { return (Rating) GetValue( RatingProperty ); }
            set { SetValue( RatingProperty, value ); }
        }

        public static readonly DependencyProperty RatingProperty =
            DependencyProperty.Register( "Rating", typeof( Rating ), typeof( RatingDisplay ), new PropertyMetadata( new Rating { Value = 0.0, VoteCount = 0 } ) );
        #endregion


        /// <summary>
        /// Creates a new RatingDisplay.
        /// </summary>
        public RatingDisplay()
        {
            InitializeComponent();
            LayoutRoot.DataContext = this;
        }
    }
}