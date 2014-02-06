// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Windows;
using System.Windows.Controls;

namespace PocketCampus.Common.Controls
{
    /// <summary>
    /// Grid that displays its elements from left to right then top to bottom, using the same width and height for each.
    /// </summary>
    public sealed class UniformWrapGrid : Panel
    {
        #region ColumnCount DependencyProperty
        public int ColumnCount
        {
            get { return (int) GetValue( ColumnCountProperty ); }
            set { SetValue( ColumnCountProperty, value ); }
        }

        public static readonly DependencyProperty ColumnCountProperty =
            DependencyProperty.Register( "ColumnCount", typeof( int ), typeof( UniformWrapGrid ), new PropertyMetadata( 1 ) );
        #endregion

        protected override Size MeasureOverride( Size availableSize )
        {
            int rowCount = (int) Math.Ceiling( (double) Children.Count / ColumnCount );
            return new Size( availableSize.Width, rowCount * ( availableSize.Width / ColumnCount ) );
        }

        protected override Size ArrangeOverride( Size finalSize )
        {
            double elemUniformSize = Math.Floor( finalSize.Width / ColumnCount );

            for ( int n = 0; n < Children.Count; n++ )
            {
                double x = ( n % ColumnCount ) * elemUniformSize;
                double y = Math.Floor( (double) n / ColumnCount ) * elemUniformSize;
                Children[n].Arrange( new Rect( x, y, elemUniformSize, elemUniformSize ) );
            }

            return finalSize;
        }
    }
}