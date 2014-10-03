// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Windows;
using System.Windows.Controls;
using PocketCampus.Moodle.Models;

namespace PocketCampus.Moodle
{
    public sealed class MoodleResourceControl : ContentControl
    {
        #region FileTemplate DependencyProperty
        public DataTemplate FileTemplate
        {
            get { return (DataTemplate) GetValue( FileTemplateProperty ); }
            set { SetValue( FileTemplateProperty, value ); }
        }

        public static readonly DependencyProperty FileTemplateProperty =
            DependencyProperty.Register( "FileTemplate", typeof( DataTemplate ), typeof( MoodleResourceControl ), new PropertyMetadata( null ) );
        #endregion

        #region FolderTemplate DependencyProperty
        public DataTemplate FolderTemplate
        {
            get { return (DataTemplate) GetValue( FolderTemplateProperty ); }
            set { SetValue( FolderTemplateProperty, value ); }
        }

        public static readonly DependencyProperty FolderTemplateProperty =
            DependencyProperty.Register( "FolderTemplate", typeof( DataTemplate ), typeof( MoodleResourceControl ), new PropertyMetadata( null ) );
        #endregion

        #region LinkTemplate DependencyProperty
        public DataTemplate LinkTemplate
        {
            get { return (DataTemplate) GetValue( LinkTemplateProperty ); }
            set { SetValue( LinkTemplateProperty, value ); }
        }

        public static readonly DependencyProperty LinkTemplateProperty =
            DependencyProperty.Register( "LinkTemplate", typeof( DataTemplate ), typeof( MoodleResourceControl ), new PropertyMetadata( null ) );
        #endregion

        protected override void OnContentChanged( object oldContent, object newContent )
        {
            base.OnContentChanged( oldContent, newContent );

            var resource = newContent as MoodleResource;
            if ( resource != null )
            {
                if ( resource.File != null )
                {
                    ContentTemplate = FileTemplate;
                    Content = resource.File;
                }
                else if ( resource.Folder != null )
                {
                    ContentTemplate = FolderTemplate;
                    Content = resource.Folder;
                }
                else
                {
                    ContentTemplate = LinkTemplate;
                    Content = resource.Link;
                }
            }
        }
    }
}