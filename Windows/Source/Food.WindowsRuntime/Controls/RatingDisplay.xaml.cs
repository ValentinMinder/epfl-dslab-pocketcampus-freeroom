using PocketCampus.Food.Models;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;

namespace PocketCampus.Food.Controls
{
    public sealed partial class RatingDisplay : UserControl
    {
        #region Value DependencyProperty
        public Rating Rating
        {
            get { return (Rating) GetValue( RatingProperty ); }
            set { SetValue( RatingProperty, value ); }
        }

        public static readonly DependencyProperty RatingProperty =
            DependencyProperty.Register( "Rating", typeof( Rating ), typeof( RatingDisplay ), new PropertyMetadata( new Rating { Value = 0.0, VoteCount = 0 } ) );
        #endregion

        #region TextStyle
        public Style TextStyle
        {
            get { return (Style) GetValue( TextStyleProperty ); }
            set { SetValue( TextStyleProperty, value ); }
        }

        public static readonly DependencyProperty TextStyleProperty =
            DependencyProperty.Register( "TextStyle", typeof( Style ), typeof( RatingDisplay ), new PropertyMetadata( null ) );
        #endregion

        public RatingDisplay()
        {
            InitializeComponent();
            Root.DataContext = this;
        }
    }
}