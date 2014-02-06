// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using PocketCampus.Main.Resources;

// Plumbing to get localized text in XAML

namespace PocketCampus.Main
{
    public class LocalizedStrings
    {
        private static AppResources _resources = new AppResources();

        public AppResources Resources
        {
            get { return _resources; }
        }
    }
}