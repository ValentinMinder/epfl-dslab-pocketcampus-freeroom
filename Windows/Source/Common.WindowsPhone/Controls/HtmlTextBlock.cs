// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Linq;
using System.Net;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Documents;
using System.Windows.Media;
using HtmlAgilityPack;

namespace PocketCampus.Common.Controls
{
    /// <summary>
    /// A TextBlock that displays HTML.
    /// </summary>
    /// <remarks>
    /// Or, rather, attempts to do so by emulating some of the most common tags.
    /// Also, it needs multiple RichTextBoxes because the maximum size for a control is 2048x2048.
    /// </remarks>
    public sealed class HtmlTextBlock : UserControl
    {
        /// <summary>
        /// The HTML to be displayed.
        /// </summary>
        public string Html
        {
            get { return (string) GetValue( HtmlProperty ); }
            set { SetValue( HtmlProperty, value ); }
        }

        public static readonly DependencyProperty HtmlProperty =
            DependencyProperty.Register( "Html", typeof( string ), typeof( HtmlTextBlock ), new PropertyMetadata( OnHtmlChanged ) );

        private static void OnHtmlChanged( DependencyObject obj, DependencyPropertyChangedEventArgs args )
        {
            var block = (HtmlTextBlock) obj;
            string html = (string) args.NewValue;
            html = HttpUtility.HtmlDecode( html );
            var root = HtmlNode.CreateNode( "<div>" + html + "</div>" );

            CleanNodes( root );

            var panel = new StackPanel();
            foreach ( var box in root.ChildNodes.SelectMany( node => node.ChildNodes ).Select( ToBox ) )
            {
                panel.Children.Add( box );
            }
            block.Content = panel;
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
                if ( node.Name == "#text" && string.IsNullOrWhiteSpace( node.InnerText ) )
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
        /// Converts an HTML node into a RichTextBox.
        /// </summary>
        private static RichTextBox ToBox( HtmlNode node )
        {
            return new RichTextBox
            {
                Blocks = { new Paragraph { Inlines = { ToInline( node ) } } },
                IsReadOnly = true,
                Margin = new Thickness( 0, 5, 0, 0 )
            };
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
                    string text = node.InnerText;
                    string url = node.GetAttributeValue( "href", "" );
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
                        },
                        NavigateUri = new Uri( url, UriKind.Absolute ),
                        TargetName = "42" // can be anything, it just needs to be set
                    };
                    link.Click += ( _, __ ) => LauncherEx.Launch( url );

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