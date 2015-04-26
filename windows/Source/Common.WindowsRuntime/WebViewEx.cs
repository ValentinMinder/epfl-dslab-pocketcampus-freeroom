using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;

namespace PocketCampus.Common
{
    public static class WebViewEx
    {
        private const string HtmlWrapper = @"<!DOCTYPE html><html><body>{0}</body></html>";

        public static string GetHtml( DependencyObject obj )
        {
            return (string) obj.GetValue( HtmlProperty );
        }

        public static void SetHtml( DependencyObject obj, string value )
        {
            obj.SetValue( HtmlProperty, value );
        }

        public static readonly DependencyProperty HtmlProperty =
            DependencyProperty.RegisterAttached( "Html", typeof( string ), typeof( WebViewEx ), new PropertyMetadata( null, OnHtmlChanged ) );

        private static void OnHtmlChanged( DependencyObject obj, DependencyPropertyChangedEventArgs args )
        {
            string content = args.NewValue.ToString();
            string html = string.Format( HtmlWrapper, content );
            ( (WebView) obj ).NavigateToString( html );
        }
    }
}