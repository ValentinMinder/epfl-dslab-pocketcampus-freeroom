using PocketCampus.Common;
using Windows.UI;
using Windows.UI.Xaml.Media;

namespace PocketCampus.Camipro
{
    /// <summary>
    /// Converts money to brushes: green for positive, red for negative and black for 0.
    /// </summary>
    public sealed class MoneyToBrushConverter : ValueConverter<double, Brush>
    {
        private static readonly Brush ZeroBrush = new SolidColorBrush( Colors.Black );
        private static readonly Brush PositiveBrush = new SolidColorBrush( Color.FromArgb( 0xFF, 0x26, 0x7F, 0x00 ) );
        private static readonly Brush NegativeBrush = new SolidColorBrush( Color.FromArgb( 0xFF, 0xC4, 0x00, 0x00 ) );

        public override Brush Convert( double value )
        {
            return value == 0.0 ? ZeroBrush
                 : value > 0.0 ? PositiveBrush
                 : NegativeBrush;
        }
    }
}
