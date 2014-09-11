﻿// Original: https://winrthtml2xaml.codeplex.com/SourceControl/latest#Html2XamlConverter.cs
// Adding as source because the NuGet package can't install for WP8.1
// Support for tables manually removed, not needed here
// Added 'strong' and 'em' as aliases for 'b' and 'i' respectively, that's what the EPFL uses

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using HtmlAgilityPack;
using Windows.UI.Xaml.Controls;

namespace Html2Xaml
{
    /// <summary>
    /// Class to convert Html text string into WinRT-compatible RichTextBlock Xaml.
    /// </summary>
    public static class Html2XamlConverter
    {
        private static Dictionary<string, Dictionary<string, string>> attributes = new Dictionary<string, Dictionary<string, string>>();
        private static Dictionary<string, TagDefinition> tags = new Dictionary<string, TagDefinition>(){
            {"div", new TagDefinition("<Span{0}>", "</Span>", true)},
            {"p", new TagDefinition("<Paragraph  LineStackingStrategy=\"MaxHeight\"{0}>", "</Paragraph>", true)},
            {"ul", new TagDefinition(parseList){MustBeTop = true}},
            {"b", new TagDefinition("<Bold{0}>")},
            {"strong", new TagDefinition("<Bold{0}>")},
            {"i", new TagDefinition("<Italic{0}>")},
            {"em", new TagDefinition("<Italic{0}>")},
            {"u", new TagDefinition("<Underline{0}>")},
            {"br", new TagDefinition("<LineBreak />", "")},
            {"blockquote", new TagDefinition("<Paragraph TextIndent=\"12\"{0}>", "</Paragraph>", true)}
        };

        /// <summary>
        /// Converts Html to Xaml.
        /// </summary>
        /// <param name="HtmlString">The Html to convert</param>
        /// <returns>Xaml markup that can be used as content in a RichTextBlock</returns>
        public static string Convert2Xaml( string HtmlString )
        {
            populateAttributes();
            HtmlDocument doc = new HtmlDocument();
            doc.LoadHtml( HtmlString );
            StringBuilder xamlString = new StringBuilder();

            foreach ( var node in doc.DocumentNode.ChildNodes )
            {
                processTopNode( xamlString, node, true );
            }

            return xamlString.ToString();
        }

        private static void processTopNode( StringBuilder xamlString, HtmlNode node, bool isTop = false )
        {
            HtmlNode nextNode = null;
            if ( !string.IsNullOrWhiteSpace( node.InnerText ) )
            {
                if ( testTop( node.FirstChild ) )
                {
                    processTopNode( xamlString, node.FirstChild );
                    return;
                }
                if ( node.Name.Equals( "blockquote", StringComparison.CurrentCultureIgnoreCase ) || node.Name.Equals( "ul", StringComparison.CurrentCultureIgnoreCase ) || node.Name.Equals( "p", StringComparison.CurrentCultureIgnoreCase ) )
                {
                    nextNode = processNode( xamlString, node, true );
                }
                else
                {
                    writeBeginningTag( xamlString, tags["p"] );
                    nextNode = processNode( xamlString, node, true );
                    writeEndTag( xamlString, tags["p"] );
                }
            }

            if ( nextNode != null )
                processTopNode( xamlString, nextNode );

            if ( !isTop && node.NextSibling != null )
            {
                if ( testTop( node.NextSibling ) )
                    processTopNode( xamlString, node.NextSibling );
                else
                {
                    writeBeginningTag( xamlString, tags["p"] );
                    nextNode = processNode( xamlString, node.NextSibling );
                    writeEndTag( xamlString, tags["p"] );
                    if ( nextNode != null )
                        processTopNode( xamlString, nextNode );
                }
            }
        }

        private static HtmlNode getNextTopNode( HtmlNode node )
        {
            if ( node.NextSibling != null )
                if ( testTop( node.NextSibling ) )
                    return node.NextSibling;
            //else
            //	return getNextTopNode(node.NextSibling);

            if ( node.ParentNode != node.OwnerDocument.DocumentNode && node.ParentNode.NextSibling != null )
                if ( testTop( node.ParentNode.NextSibling ) )
                    return node.ParentNode.NextSibling;
            //else
            //	return getNextTopNode(node.ParentNode.NextSibling);
            return null;
        }

        private static bool testTop( HtmlNode node )
        {
            if ( node == null )
                return false;
            return ( tags.ContainsKey( node.Name ) && tags[node.Name].MustBeTop );
        }

        private static HtmlNode processNode( StringBuilder xamlString, HtmlNode node, bool isTop = false )
        {
            string tagName = node.Name.ToLower();

            HtmlNode top = null;
            if ( tags.ContainsKey( tagName ) )
            {
                if ( tags[tagName].MustBeTop && !isTop )
                    return node;

                if ( tags[tagName].IsCustom )
                {
                    tags[tagName].CustomAction( xamlString, node );
                    return null;
                }
                else
                {
                    writeBeginningTag( xamlString, tags[tagName] );

                    if ( node.HasChildNodes )
                        top = processNode( xamlString, node.FirstChild );

                    writeEndTag( xamlString, tags[tagName] );
                }
            }
            else
            {
                if ( node.NodeType == HtmlNodeType.Text )
                    xamlString.Append( node.InnerText );

                if ( node.HasChildNodes )
                    top = processNode( xamlString, node.FirstChild );
            }

            if ( top == null && node.NextSibling != null && !isTop )
                top = processNode( xamlString, node.NextSibling );

            return top;
        }

        private static void writeEndTag( StringBuilder xamlString, TagDefinition tag )
        {
            xamlString.Append( tag.EndXamlTag );
        }

        private static void writeBeginningTag( StringBuilder xamlString, TagDefinition tag )
        {
            string attrs = string.Empty;
            if ( tag.Attributes.Count > 0 )
                attrs = " " + string.Join( " ", tag.Attributes.Select( a => string.Format( "{0}=\"{1}\"", a.Key, a.Value ) ).ToArray() );

            xamlString.Append( string.Format( tag.BeginXamlTag, attrs ) );
        }

        private static void populateAttributes()
        {
            foreach ( var attribute in attributes )
            {
                if ( tags.ContainsKey( attribute.Key ) )
                    foreach ( var attr in attribute.Value )
                        if ( !tags[attribute.Key].Attributes.ContainsKey( attr.Key ) )
                            tags[attribute.Key].Attributes.Add( attr.Key, attr.Value );
            }
        }
        /// <summary>
        /// Converts Html to Xaml including attributes that can be used to determine the formatting of individual elements.
        /// <example><code>
        /// string Xaml = Html2XamlConverter.Convert2Xaml(html, new Dictionary<string, Dictionary<string, string>> { 
        /// 					{ "p", new Dictionary<string, string> { { "Margin", "0,10,0,0" } } },
        /// 					{ "i", new Dictionary<string, string> { { "Foreground", "#FF663C00"}}}
        /// 					});
        /// </code>
        /// </example>
        /// </summary>
        /// <param name="HtmlString">The Html to convert</param>
        /// <param name="TagAttributes">A dictionary that allows you to add attributes to the Xaml being emitted by this method. 
        /// The first key is the Html tag you want to add formatting to. The dictionary associated with that tag allows you to set
        /// multiple attributes and values associated with that Html tag.</param>
        /// <returns>Xaml markup that can be used as content in a RichTextBlock</returns>
        public static string Convert2Xaml( string HtmlString, Dictionary<string, Dictionary<string, string>> TagAttributes )
        {
            if ( TagAttributes != null )
                attributes = TagAttributes;
            return Convert2Xaml( HtmlString );
        }

        private static void parseList( StringBuilder xamlString, HtmlNode listNode )
        {
            // Yeah, this actually works out okay, though hard-coded margins and diamond symbol kinda suck.
            foreach ( var li in listNode.Descendants( "li" ) )
            {
                xamlString.Append( "<Paragraph Margin=\"24,0,0,0\" TextIndent=\"-24\"><Run FontFamily=\"Segoe UI Symbol\">&#x2B27;</Run><Span><Run Text=\"  \"/>" );
                processNode( xamlString, li.FirstChild );
                xamlString.Append( "</Span></Paragraph>" );
            }
        }

        private static int setCellAttributes( int currentRow, int currentColumn, HtmlNode cellNode, TextBlock cell )
        {
            int rowSpan = cellNode.GetAttributeValue( "rowspan", 0 );
            int colSpan = cellNode.GetAttributeValue( "colspan", 0 );
            if ( rowSpan > 0 )
            {
                Grid.SetRowSpan( cell, rowSpan );
            }
            if ( colSpan > 0 )
            {
                Grid.SetColumnSpan( cell, colSpan );
            }
            if ( currentRow > 0 )
            {
                Grid.SetRow( cell, currentRow );
            }
            if ( currentColumn > 0 )
            {
                Grid.SetColumn( cell, currentColumn );
            }
            cell.Text = cellNode.InnerText;

            return colSpan + currentColumn;
        }
    }
}