using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;

namespace PocketCampus.Food.Controls
{
    public sealed partial class RatingPicker : UserControl
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


        public RatingPicker()
        {
            InitializeComponent();
            Root.DataContext = this;
        }
    }
}