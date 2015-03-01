using Windows.Foundation;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;

namespace PocketCampus.Common.Controls
{
    public sealed class HorizontalUniformGrid : Panel
    {
        public int ColumnCount
        {
            get { return (int) GetValue( ColumnCountProperty ); }
            set { SetValue( ColumnCountProperty, value ); }
        }

        public static readonly DependencyProperty ColumnCountProperty =
            DependencyProperty.Register( "ColumnCount", typeof( int ), typeof( HorizontalUniformGrid ), new PropertyMetadata( 0 ) );

        public double ElementMargin
        {
            get { return (double) GetValue( ElementMarginProperty ); }
            set { SetValue( ElementMarginProperty, value ); }
        }

        public static readonly DependencyProperty ElementMarginProperty =
            DependencyProperty.Register( "ElementMargin", typeof( double ), typeof( HorizontalUniformGrid ), new PropertyMetadata( 0.0 ) );


        private double _elementSize;

        protected override Size MeasureOverride( Size availableSize )
        {
            _elementSize = ( availableSize.Width - ( ElementMargin * ( ColumnCount - 1 ) ) ) / ColumnCount;

            var childSize = new Size( _elementSize, _elementSize );
            foreach ( var child in Children )
            {
                child.Measure( childSize );
            }

            int rowCount = Children.Count / ColumnCount;
            double usedHeight = rowCount * _elementSize + ( rowCount - 1 ) * ElementMargin;

            return new Size( availableSize.Width, usedHeight );
        }

        protected override Size ArrangeOverride( Size finalSize )
        {
            int x = 0;
            int y = 0;

            foreach ( var child in Children )
            {
                child.Arrange( new Rect( x * ( _elementSize + ElementMargin ), y * ( _elementSize + ElementMargin ), _elementSize, _elementSize ) );
                x++;
                if ( x == ColumnCount )
                {
                    x = 0;
                    y++;
                }
            }

            return finalSize;
        }
    }
}
