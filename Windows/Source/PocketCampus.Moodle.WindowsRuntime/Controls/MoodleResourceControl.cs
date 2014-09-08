// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using PocketCampus.Moodle.Models;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;

namespace PocketCampus.Moodle.Controls
{
    public sealed class MoodleResourceControl : ContentControl
    {
        #region FileTemplate
        public DataTemplate FileTemplate
        {
            get { return (DataTemplate) GetValue( FileTemplateProperty ); }
            set { SetValue( FileTemplateProperty, value ); }
        }

        public static readonly DependencyProperty FileTemplateProperty =
            DependencyProperty.Register( "FileTemplate", typeof( DataTemplate ), typeof( MoodleResourceControl ), new PropertyMetadata( null ) );
        #endregion

        #region FolderTemplate
        public DataTemplate FolderTemplate
        {
            get { return (DataTemplate) GetValue( FolderTemplateProperty ); }
            set { SetValue( FolderTemplateProperty, value ); }
        }

        public static readonly DependencyProperty FolderTemplateProperty =
            DependencyProperty.Register( "FolderTemplate", typeof( DataTemplate ), typeof( MoodleResourceControl ), new PropertyMetadata( null ) );
        #endregion

        #region LinkTemplate
        public DataTemplate LinkTemplate
        {
            get { return (DataTemplate) GetValue( LinkTemplateProperty ); }
            set { SetValue( LinkTemplateProperty, value ); }
        }

        public static readonly DependencyProperty LinkTemplateProperty =
            DependencyProperty.Register( "LinkTemplate", typeof( DataTemplate ), typeof( MoodleResourceControl ), new PropertyMetadata( null ) );
        #endregion

        #region Resource
        public MoodleResource Resource
        {
            get { return (MoodleResource) GetValue( ResourceProperty ); }
            set { SetValue( ResourceProperty, value ); }
        }

        // Using a DependencyProperty as the backing store for Resource.  This enables animation, styling, binding, etc...
        public static readonly DependencyProperty ResourceProperty =
            DependencyProperty.Register( "Resource", typeof( MoodleResource ), typeof( MoodleResourceControl ), new PropertyMetadata( null, OnResourceChanged ) );

        private static void OnResourceChanged( DependencyObject obj, DependencyPropertyChangedEventArgs args )
        {
            var control = (MoodleResourceControl) obj;
            var resource = (MoodleResource) args.NewValue;

            if ( resource.File != null )
            {
                control.ContentTemplate = control.FileTemplate;
                control.Content = resource.File;
            }
            else if ( resource.Folder != null )
            {
                control.ContentTemplate = control.FolderTemplate;
                control.Content = resource.Folder;
            }
            else
            {
                control.ContentTemplate = control.LinkTemplate;
                control.Content = resource.Link;
            }
        }
        #endregion
    }
}