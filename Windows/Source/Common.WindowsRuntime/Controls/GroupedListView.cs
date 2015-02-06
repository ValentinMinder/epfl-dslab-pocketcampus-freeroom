using System;
using Windows.UI;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Data;
using Windows.UI.Xaml.Markup;
using Windows.UI.Xaml.Media;

namespace PocketCampus.Common.Controls
{
    // Simpler way to deal with SemanticZoom
    public sealed class GroupedListView : ContentControl
    {
        private static readonly Brush ZoomedOutBackgroundBrush = new SolidColorBrush( new Color { A = 192, R = 0, G = 0, B = 0 } );
        private static readonly Brush ZoomedOutForegroundBrush = new SolidColorBrush( Colors.White );
        private static readonly Thickness ZoomedOutViewPadding = new Thickness( 19, 19, 0, 0 );
        private const double ZoomedInViewTopMargin = 12;
        private const double ZoomedInGroupFooterSize = 19;
        private const double ZoomedInGroupLeftMargin = 12;
        private const double ZoomedOutGroupBottomMargin = 19;

        #region ItemsViewSource
        public CollectionViewSource ItemsViewSource
        {
            get { return (CollectionViewSource) GetValue( ItemsViewSourceProperty ); }
            set { SetValue( ItemsViewSourceProperty, value ); }
        }

        public static readonly DependencyProperty ItemsViewSourceProperty =
            DependencyProperty.Register( "ItemsViewSource", typeof( CollectionViewSource ), typeof( GroupedListView ), new PropertyMetadata( null ) );
        #endregion

        #region ItemTemplate
        public DataTemplate ItemTemplate
        {
            get { return (DataTemplate) GetValue( ItemTemplateProperty ); }
            set { SetValue( ItemTemplateProperty, value ); }
        }

        public static readonly DependencyProperty ItemTemplateProperty =
            DependencyProperty.Register( "ItemTemplate", typeof( DataTemplate ), typeof( GroupedListView ), new PropertyMetadata( null ) );
        #endregion

        #region SelectedItem
        public object SelectedItem
        {
            get { return (object) GetValue( SelectedItemProperty ); }
            set { SetValue( SelectedItemProperty, value ); }
        }

        public static readonly DependencyProperty SelectedItemProperty =
            DependencyProperty.Register( "SelectedItem", typeof( object ), typeof( GroupedListView ), new PropertyMetadata( null ) );
        #endregion

        #region GroupSubheaderTemplate
        public DataTemplate GroupSubheaderTemplate
        {
            get { return (DataTemplate) GetValue( GroupSubheaderTemplateProperty ); }
            set { SetValue( GroupSubheaderTemplateProperty, value ); }
        }

        public static readonly DependencyProperty GroupSubheaderTemplateProperty =
            DependencyProperty.Register( "GroupSubheaderTemplate", typeof( DataTemplate ), typeof( GroupedListView ), new PropertyMetadata( null ) );
        #endregion

        #region GroupKeyPath
        public string GroupKeyPath
        {
            get { return (string) GetValue( GroupKeyPathProperty ); }
            set { SetValue( GroupKeyPathProperty, value ); }
        }

        public static readonly DependencyProperty GroupKeyPathProperty =
            DependencyProperty.Register( "GroupKeyPath", typeof( string ), typeof( GroupedListView ), new PropertyMetadata( null ) );
        #endregion

        public GroupedListView()
        {
            Name = Guid.NewGuid().ToString();
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
                ItemTemplate = ItemTemplate,
                GroupStyle = // ListView.GroupStyle is a collection, yes...
                {
                    new GroupStyle
                    {
                        HeaderTemplate = GetGroupHeaderTemplate()
                    }
                },
                // HACK: The group header template contains a top margin, so we cancel the first one here
                Padding = new Thickness( 0, ZoomedInViewTopMargin - ZoomedInGroupFooterSize, 0, 0 )
            };
            // Weird way CollectionViewSources have to be used
            view.SetBinding
            (
                ListView.ItemsSourceProperty,
                new Binding { Source = ItemsViewSource }
            );
            view.SetBinding
            (
                ListView.SelectedItemProperty,
                new Binding { Source = this, Path = new PropertyPath( "SelectedItem" ) }
            );
            return view;
        }

        private ISemanticZoomInformation GetZoomedOutView()
        {
            var view = new ListView
            {
                ItemTemplate = GetGroupItemTemplate(),
                Foreground = ZoomedOutForegroundBrush,
                Background = ZoomedOutBackgroundBrush,
                Padding = ZoomedOutViewPadding
            };
            // weird way CollectionViewSources have to be used
            view.SetBinding
            (
                ListView.ItemsSourceProperty,
                new Binding { Source = ItemsViewSource, Path = new PropertyPath( "CollectionGroups" ) }
            );
            view.SetBinding
            (
                ListView.SelectedItemProperty,
                new Binding { Source = this, Path = new PropertyPath( "SelectedItem" ) }
            );
            return view;
        }

        // DataTemplates can't be created in code directly...

        private DataTemplate GetGroupHeaderTemplate()
        {
            if ( GroupSubheaderTemplate == null )
            {
                return (DataTemplate) XamlReader.Load(
              @"<DataTemplate xmlns=""http://schemas.microsoft.com/winfx/2006/xaml/presentation"">
                    <TextBlock Text=""{Binding " + GroupKeyPath + @"}""
                               Style=""{StaticResource AppGroupHeaderTextBlockStyle}""
                               Foreground=""{ThemeResource ApplicationForegroundThemeBrush}""
                               Margin=""" + ZoomedInGroupLeftMargin + "," + ZoomedInGroupFooterSize + @",0,0"" />
                </DataTemplate>" );
            }

            return (DataTemplate) XamlReader.Load(
          @"<DataTemplate xmlns=""http://schemas.microsoft.com/winfx/2006/xaml/presentation"">
                <StackPanel>
                    <TextBlock Text=""{Binding " + GroupKeyPath + @"}""
                               Style=""{StaticResource AppGroupHeaderTextBlockStyle}""
                               Foreground=""{ThemeResource ApplicationForegroundThemeBrush}""
                               Margin=""" + ZoomedInGroupLeftMargin + "," + ZoomedInGroupFooterSize + @",0,0"" />
                    <ContentControl Content=""{Binding}""
                                    ContentTemplate=""{Binding GroupSubheaderTemplate, ElementName=" + Name + @"}""
                                    Margin=""" + ZoomedInGroupLeftMargin + @",0,0,0"" />
                </StackPanel>
            </DataTemplate>" );
        }

        private DataTemplate GetGroupItemTemplate()
        {
            return (DataTemplate) XamlReader.Load(
           @"<DataTemplate xmlns=""http://schemas.microsoft.com/winfx/2006/xaml/presentation"">
                <TextBlock Text=""{Binding Group." + GroupKeyPath + @"}""
                           FontSize=""{StaticResource TextStyleExtraLargeFontSize}""
                           Margin=""0,0,0," + ZoomedOutGroupBottomMargin + @""" />
            </DataTemplate>" );
        }
    }
}