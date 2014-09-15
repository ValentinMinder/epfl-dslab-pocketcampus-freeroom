using ThinMvvm;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;

namespace PocketCampus.Common.Controls
{
    public sealed class CacheStatusDisplay : Control
    {
        public CacheStatus Status
        {
            get { return (CacheStatus) GetValue( StatusProperty ); }
            set { SetValue( StatusProperty, value ); }
        }

        public static readonly DependencyProperty StatusProperty =
            DependencyProperty.Register( "Status", typeof( CacheStatus ), typeof( CacheStatusDisplay ), new PropertyMetadata( CacheStatus.NoData, OnStatusChanged ) );

        private static void OnStatusChanged( DependencyObject obj, DependencyPropertyChangedEventArgs args )
        {
            ( (CacheStatusDisplay) obj ).Visibility = (CacheStatus) args.NewValue == CacheStatus.Used ? Visibility.Visible : Visibility.Collapsed;
        }


        public CacheStatusDisplay()
        {
            DefaultStyleKey = typeof( CacheStatusDisplay );
        }
    }
}