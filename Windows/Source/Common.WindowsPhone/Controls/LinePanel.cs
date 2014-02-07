// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Windows;
using System.Windows.Controls;

namespace PocketCampus.Common.Controls
{
    /// <summary>
    /// Panel that displays all its children in one single line.
    /// Unlike the StackPanel, it is bounded.
    /// </summary>
    public sealed class LinePanel : Panel
    {
        #region Orientation DependencyProperty
        public Orientation Orientation
        {
            get { return (Orientation) GetValue( OrientationProperty ); }
            set { SetValue( OrientationProperty, value ); }
        }

        public static readonly DependencyProperty OrientationProperty =
            DependencyProperty.Register( "Orientation", typeof( Orientation ), typeof( LinePanel ), new PropertyMetadata( Orientation.Vertical ) );
        #endregion

        protected override Size MeasureOverride( Size availableSize )
        {
            VisitChildren( availableSize, ( child, rect ) => child.Measure( new Size( rect.Width, rect.Height ) ) );

            double width = double.IsInfinity( availableSize.Width ) ? 0 : availableSize.Width;
            double height = double.IsInfinity( availableSize.Height ) ? 0 : availableSize.Height;
            return new Size( width, height );
        }

        protected override Size ArrangeOverride( Size finalSize )
        {
            VisitChildren( finalSize, ( child, rect ) => child.Arrange( rect ) );
            return finalSize;
        }

        private void VisitChildren( Size size, Action<UIElement, Rect> action )
        {
            if ( Orientation == Orientation.Vertical )
            {
                double elementHeight = size.Height / Children.Count;
                for ( int n = 0; n < Children.Count; n++ )
                {
                    var rect = new Rect( 0, n * elementHeight, size.Width, elementHeight );
                    action( Children[n], rect );
                }
            }
            else
            {
                double elementWidth = size.Width / Children.Count;
                for ( int n = 0; n < Children.Count; n++ )
                {
                    var rect = new Rect( n * elementWidth, 0, elementWidth, size.Height );
                    action( Children[n], rect );
                }
            }
        }
    }
}