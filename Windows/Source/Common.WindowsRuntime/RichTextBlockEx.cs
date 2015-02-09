// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Linq;
using System.Net;
using HtmlAgilityPack;
using Windows.UI;
using Windows.UI.Text;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Documents;
using Windows.UI.Xaml.Media;

namespace PocketCampus.Common
{
    public static class RichTextBlockEx
    {
        public static string GetHtml( DependencyObject obj )
        {
            return (string) obj.GetValue( HtmlProperty );
        }

        public static void SetHtml( DependencyObject obj, string value )
        {
            obj.SetValue( HtmlProperty, value );
        }

        // Using a DependencyProperty as the backing store for Html.  This enables animation, styling, binding, etc...
        public static readonly DependencyProperty HtmlProperty =
            DependencyProperty.RegisterAttached( "Html", typeof( string ), typeof( RichTextBlockEx ), new PropertyMetadata( null, OnHtmlChanged ) );

        private static void OnHtmlChanged( DependencyObject obj, DependencyPropertyChangedEventArgs args )
        {
            var richBlock = (RichTextBlock) obj;
            string html = WebUtility.HtmlDecode( (string) args.NewValue );
            var root = HtmlNode.CreateNode( "<div>" + html + "</div>" );

            CleanNodes( root );

            foreach ( var inline in root.ChildNodes.Select( ToInline ) )
            {
                richBlock.Blocks.Add( new Paragraph { Inlines = { inline } } );
            }
        }

        /// <summary>
        /// Cleans the children of an HTML node by removing useless nodes.
        /// </summary>
        private static void CleanNodes( HtmlNode root )
        {
            // Remove empty nodes
            for ( int n = 0; n < root.ChildNodes.Count; n++ )
            {
                var node = root.ChildNodes[n];
                if ( node.NodeType == HtmlNodeType.Text && string.IsNullOrWhiteSpace( node.InnerText ) )
                {
                    node.Remove();
                    n--;
                }
            }

            // Remove consecutive line breaks
            bool wasLineBreak = false;
            for ( int n = 0; n < root.ChildNodes.Count; n++ )
            {
                var node = root.ChildNodes[n];
                if ( node.Name == "br" )
                {
                    if ( wasLineBreak )
                    {
                        node.Remove();
                        n--;
                    }
                    wasLineBreak = true;
                }
                else
                {
                    wasLineBreak = false;
                }
            }
        }

        /// <summary>
        /// Converts an HTML node into an Inline.
        /// </summary>
        private static Inline ToInline( HtmlNode node )
        {
            switch ( node.Name )
            {
                case "br":
                    return new LineBreak();

                case "a":
                    string url = node.GetAttributeValue( "href", "" );
                    if ( string.IsNullOrWhiteSpace( url ) ) // happens sometimes... broken HTML...
                    {
                        break;
                    }

                    string text = node.InnerText;
                    if ( string.IsNullOrWhiteSpace( text ) )
                    {
                        text = url;
                    }

                    var link = new Hyperlink
                    {
                        Inlines = 
                        {
                            new Run 
                            {
                                Text = text,
                                Foreground = new SolidColorBrush( Colors.Blue )
                            } 
                        }
                    };
                    link.Click += ( _, __ ) => LauncherEx.Launch( new Uri( url, UriKind.Absolute ) );

                    return new Span
                    {
                        Inlines =
                        {
                            new Run { Text = " " },
                            link,
                            new Run { Text = " " },
                        }
                    };

                case "strong":
                case "b":
                    var bold = new Bold();
                    foreach ( var child in node.ChildNodes )
                    {
                        bold.Inlines.Add( ToInline( child ) );
                    }
                    return bold;

                case "em":
                case "i":
                    var italic = new Italic();
                    foreach ( var child in node.ChildNodes )
                    {
                        italic.Inlines.Add( ToInline( child ) );
                    }
                    return italic;

                case "ul":
                    var unorderedList = new Span();
                    foreach ( var child in node.ChildNodes )
                    {
                        var listElem = new Span();
                        listElem.Inlines.Add( new Run { Text = "● " } );
                        listElem.Inlines.Add( ToInline( child ) );
                        unorderedList.Inlines.Add( listElem );
                        unorderedList.Inlines.Add( new LineBreak() );
                    }
                    return unorderedList;

                case "ol":
                    var orderedList = new Span();
                    for ( int n = 0; n < node.ChildNodes.Count; n++ )
                    {
                        var listElem = new Span();
                        listElem.Inlines.Add( new Run { Text = n.ToString() + " " } );
                        listElem.Inlines.Add( ToInline( node.ChildNodes[n] ) );
                        orderedList.Inlines.Add( listElem );
                        orderedList.Inlines.Add( new LineBreak() );
                    }
                    return orderedList;

                case "h1":
                    return new Run
                    {
                        Text = node.InnerText + Environment.NewLine,
                        FontWeight = FontWeights.Bold,
                        FontSize = 32
                    };

                case "h2":
                    return new Run
                    {
                        Text = node.InnerText + Environment.NewLine,
                        FontWeight = FontWeights.SemiBold,
                        FontSize = 24
                    };

                case "h3":
                    return new Run
                    {
                        Text = node.InnerText + Environment.NewLine,
                        FontWeight = FontWeights.SemiBold,
                        FontSize = 19
                    };

                case "h4":
                    return new Run
                    {
                        Text = node.InnerText + Environment.NewLine,
                        FontWeight = FontWeights.Medium,
                        FontSize = 17
                    };

                case "h5":
                    return new Run
                    {
                        Text = node.InnerText + Environment.NewLine,
                        FontWeight = FontWeights.Medium,
                        FontSize = 16
                    };

                case "div":
                case "p":
                case "blockquote":
                    var container = new Span();
                    foreach ( var child in node.ChildNodes )
                    {
                        container.Inlines.Add( ToInline( child ) );
                    }
                    container.Inlines.Add( new LineBreak() );
                    container.Inlines.Add( new LineBreak() );
                    return container;
            }

            return new Run { Text = node.PreviousSibling == null ? node.InnerText.TrimStart() : node.InnerText };
        }

    }
}