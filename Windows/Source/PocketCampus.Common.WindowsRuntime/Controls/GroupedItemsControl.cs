using Windows.UI;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Data;
using Windows.UI.Xaml.Media;

namespace PocketCampus.Common.Controls
{
    // Simpler way to deal with SemanticZoom
    public sealed class GroupedItemsControl : ContentControl
    {
        private static readonly Style StretchListViewItemStyle = new Style( typeof( ListViewItem ) )
        {
            Setters = 
            {
                new Setter( ListViewItem.HorizontalAlignmentProperty, HorizontalAlignment.Stretch ),
                new Setter( ListViewItem.HorizontalContentAlignmentProperty, HorizontalAlignment.Stretch )
            }
        };
        private static readonly Brush ZoomedOutBackgroundBrush = new SolidColorBrush( new Color { A = 192, R = 0, G = 0, B = 0 } );
        private static readonly Brush ZoomedOutForegroundBrush = new SolidColorBrush( Colors.White );
        private static readonly Thickness ZoomedOutViewPadding = new Thickness( 12 );

        #region ItemsViewSource DependencyProperty
        public CollectionViewSource ItemsViewSource
        {
            get { return (CollectionViewSource) GetValue( ItemsViewSourceProperty ); }
            set { SetValue( ItemsViewSourceProperty, value ); }
        }

        public static readonly DependencyProperty ItemsViewSourceProperty =
            DependencyProperty.Register( "ItemsViewSource", typeof( CollectionViewSource ), typeof( GroupedItemsControl ), new PropertyMetadata( null ) );
        #endregion

        #region ItemTemplate
        public DataTemplate ItemTemplate
        {
            get { return (DataTemplate) GetValue( ItemTemplateProperty ); }
            set { SetValue( ItemTemplateProperty, value ); }
        }

        public static readonly DependencyProperty ItemTemplateProperty =
            DependencyProperty.Register( "ItemTemplate", typeof( DataTemplate ), typeof( GroupedItemsControl ), new PropertyMetadata( null ) );
        #endregion

        #region GroupHeaderTemplate
        public DataTemplate GroupHeaderTemplate
        {
            get { return (DataTemplate) GetValue( GroupHeaderTemplateProperty ); }
            set { SetValue( GroupHeaderTemplateProperty, value ); }
        }

        public static readonly DependencyProperty GroupHeaderTemplateProperty =
            DependencyProperty.Register( "GroupHeaderTemplate", typeof( DataTemplate ), typeof( GroupedItemsControl ), new PropertyMetadata( null ) );
        #endregion

        #region GroupItemTemplate
        public DataTemplate GroupItemTemplate
        {
            get { return (DataTemplate) GetValue( GroupItemTemplateProperty ); }
            set { SetValue( GroupItemTemplateProperty, value ); }
        }

        public static readonly DependencyProperty GroupItemTemplateProperty =
            DependencyProperty.Register( "GroupItemTemplate", typeof( DataTemplate ), typeof( GroupedItemsControl ), new PropertyMetadata( null ) );
        #endregion

        public GroupedItemsControl()
        {
            Loaded += ( _, __ ) =>
            {
                HorizontalAlignment = HorizontalAlignment.Stretch;
                HorizontalContentAlignment = HorizontalAlignment.Stretch;
                Content = new SemanticZoom
                {
                    ZoomedInView = GetZoomedInView(),
                    ZoomedOutView = GetZoomedOutView()
                };
            };
        }

        private ISemanticZoomInformation GetZoomedInView()
        {
            var view = new ListView
            {
                ItemContainerStyle = StretchListViewItemStyle,
                ItemTemplate = this.ItemTemplate,
                GroupStyle = { new GroupStyle { HidesIfEmpty = true, HeaderTemplate = this.GroupHeaderTemplate } }
            };
            // weird way CollectionViewSources have to be used
            view.SetBinding
            (
                ListView.ItemsSourceProperty,
                new Binding { Source = this.ItemsViewSource }
            );
            return view;
        }

        private ISemanticZoomInformation GetZoomedOutView()
        {
            var view = new ListView
            {
                ItemTemplate = this.GroupItemTemplate,

                ItemContainerStyle = StretchListViewItemStyle,
                Foreground = ZoomedOutForegroundBrush,
                Background = ZoomedOutBackgroundBrush,
                Padding = ZoomedOutViewPadding
            };
            // weird way CollectionViewSources have to be used
            view.SetBinding
            (
                ListView.ItemsSourceProperty,
                new Binding { Source = this.ItemsViewSource, Path = new PropertyPath( "CollectionGroups" ) }
            );
            return view;
        }
    }
}