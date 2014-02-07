// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Windows;
using System.Windows.Media;
using PocketCampus.Camipro.ViewModels;
using PocketCampus.Common;

namespace PocketCampus.Camipro
{
    /// <summary>
    /// Converts decimals to brushes: green for positive, red for negative and black for 0.
    /// </summary>
    public sealed class DoubleToBrushConverter : ValueConverter<double, Brush>
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

    /// <summary>
    /// Converts an e-mail sending status to a visibility, for the "request e-mail" button: if it hasn't been requested, it's visible.
    /// </summary>
    public sealed class EmailNotRequestedToVisibilityConverter : ValueConverter<EmailSendingStatus, Visibility>
    {
        public override Visibility Convert( EmailSendingStatus value )
        {
            return value == EmailSendingStatus.NoneRequested ? Visibility.Visible : Visibility.Collapsed;
        }
    }
}