// Original: https://winrthtml2xaml.codeplex.com/SourceControl/latest#TagDefinition.cs
// Adding as source because the NuGet package can't install for WP8.1

using System;
using System.Collections.Generic;
using System.Text;
using HtmlAgilityPack;

namespace Html2Xaml
{
    internal class TagDefinition
    {
        public TagDefinition()
        {
            this.Attributes = new Dictionary<string, string>();
        }

        public TagDefinition( string XamlTag )
            : this()
        {
            this.BeginXamlTag = XamlTag;
            this.EndXamlTag = string.Format( XamlTag, string.Empty ).Replace( "<", "</" );
        }
        public TagDefinition( string XamlBeginTag, string XamlEndTag, bool MustBeTop = false )
            : this()
        {
            this.BeginXamlTag = XamlBeginTag;
            this.EndXamlTag = XamlEndTag;
            this.MustBeTop = MustBeTop;
        }
        public TagDefinition( Action<StringBuilder, HtmlNode> CustomAction )
            : this()
        {
            this.CustomAction = CustomAction;
            this.IsCustom = true;
        }
        public string BeginXamlTag { get; set; }
        public string EndXamlTag { get; set; }
        public Dictionary<string, string> Attributes { get; set; }
        public bool MustBeTop { get; set; }
        public bool IsCustom { get; set; }
        public Action<StringBuilder, HtmlNode> CustomAction { get; set; }
    }
}