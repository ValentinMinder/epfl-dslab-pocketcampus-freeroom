using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;

namespace PocketCampus.Common
{
    //ListView.GroupStyle is a collection, we want to set it as an item
    public sealed class ListViewEx
    {
        public static GroupStyle GetGroupStyle( DependencyObject obj )
        {
            return (GroupStyle) obj.GetValue( GroupStyleProperty );
        }

        public static void SetGroupStyle( DependencyObject obj, GroupStyle value )
        {
            obj.SetValue( GroupStyleProperty, value );
        }

        public static readonly DependencyProperty GroupStyleProperty =
            DependencyProperty.RegisterAttached( "GroupStyle", typeof( GroupStyle ), typeof( ListViewEx ), new PropertyMetadata( null, OnGroupStyleChanged ) );

        private static void OnGroupStyleChanged( DependencyObject obj, DependencyPropertyChangedEventArgs args )
        {
            var view = (ListView) obj;

            GroupStyle gs = args.OldValue as GroupStyle;
            if ( gs != null )
            {
                view.GroupStyle.Remove( gs );
            }

            gs = args.NewValue as GroupStyle;
            if ( gs != null )
            {
                view.GroupStyle.Add( gs );
            }
        }
    }
}