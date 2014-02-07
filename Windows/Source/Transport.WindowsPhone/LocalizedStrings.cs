// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using PocketCampus.Transport.Resources;

// Plumbing to get localized text in XAML

namespace PocketCampus.Transport
{
    public class LocalizedStrings
    {
        private static readonly PluginResources _resources = new PluginResources();

        public PluginResources Resources
        {
            get { return _resources; }
        }
    }
}