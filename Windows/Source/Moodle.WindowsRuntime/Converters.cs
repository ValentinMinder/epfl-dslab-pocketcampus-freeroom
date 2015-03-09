// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Collections.Generic;
using PocketCampus.Common;
using PocketCampus.Common.Controls;
using PocketCampus.Moodle.Models;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Media;
using Windows.UI.Xaml.Media.Imaging;

namespace PocketCampus.Moodle
{
    public sealed class FileToIconElementConverter : ValueConverter<MoodleFile, UIElement>
    {
        private static readonly Dictionary<string, string> Aliases = new Dictionary<string, string>
        {
            {"ppt", "pptx"},
            {"doc", "docx"},
            {"xls", "xlsx"}
        };

        private const int DefaultSquareSize = 48;
        private const int IconWidth = 38;
        private const int IconHeight = 46;

        private readonly ResourceDictionary _resources = new ResourceDictionary
        {
            Source = new Uri( "ms-appx:///PocketCampus.Moodle.WindowsRuntime/Icons.xaml", UriKind.Absolute )
        };


        public override UIElement Convert( MoodleFile value )
        {
            string ext = value.Extension;
            if ( Aliases.ContainsKey( ext ) )
            {
                ext = Aliases[ext];
            }

            string resourceKey = "FileIcon_" + ext;
            if ( _resources.ContainsKey( resourceKey ) )
            {
                return new Icon
                {
                    Data = (string) _resources[resourceKey],
                    IconHeight = IconHeight,
                    IconWidth = IconWidth
                };
            }

            if ( value.IconUrl != null )
            {
                return new Viewbox
                {
                    Stretch = Stretch.Uniform,
                    Child = new Image
                    {
                        Source =
                            new BitmapImage( new Uri( value.IconUrl.Replace( "{size}", DefaultSquareSize.ToString() ),
                                UriKind.Absolute ) )
                    }
                };
            }

            return new Icon
            {
                Data = (string) _resources["DefaultFileIcon"],
                IconHeight = IconHeight,
                IconWidth = IconWidth
            };
        }
    }

    public sealed class PathComponentsToStringConverter : ValueConverter<string[], string>
    {
        private const string Separator = " > ";

        public override string Convert( string[] value )
        {
            return string.Join( Separator, value );
        }
    }
}