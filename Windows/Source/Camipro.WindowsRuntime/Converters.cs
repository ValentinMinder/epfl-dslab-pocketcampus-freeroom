// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using PocketCampus.Common;
using Windows.UI;
using Windows.UI.Xaml.Media;

namespace PocketCampus.Camipro
{
    public sealed class MoneyToBrushConverter : ValueConverter<double, Brush>
    {
        private static readonly Brush ZeroBrush = new SolidColorBrush( Colors.Black );
        private static readonly Brush PositiveBrush = new SolidColorBrush( Colors.DarkGreen );
        private static readonly Brush NegativeBrush = new SolidColorBrush( Colors.DarkRed );

        public override Brush Convert( double value )
        {
            return value == 0.0 ? ZeroBrush
                 : value > 0.0 ? PositiveBrush
                 : NegativeBrush;
        }
    }
}