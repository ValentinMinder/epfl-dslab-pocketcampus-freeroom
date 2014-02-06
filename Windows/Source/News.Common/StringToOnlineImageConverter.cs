// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using PocketCampus.News.Models;
using ThriftSharp;

namespace PocketCampus.News
{
    public sealed class StringToOnlineImageConverter : ThriftValueConverter<string, OnlineImage>
    {
        protected override OnlineImage Convert( string value )
        {
            return new OnlineImage( value );
        }

        protected override string ConvertBack( OnlineImage value )
        {
            throw new NotSupportedException();
        }
    }
}