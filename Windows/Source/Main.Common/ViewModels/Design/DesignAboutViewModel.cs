// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

// Design data for the AboutViewModel

using System.Reflection;

namespace PocketCampus.Main.ViewModels.Design
{
    public sealed class DesignAboutViewModel
    {
        /// <summary>
        /// Gets the app version.
        /// </summary>
        public string AppVersion
        {
            get { return typeof( AboutViewModel ).GetTypeInfo().Assembly.GetName().Version.ToString( 2 ); }
        }
    }
}