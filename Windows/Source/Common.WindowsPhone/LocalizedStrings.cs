// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using PocketCampus.Common.Resources;

// Plumbing to get localized text in XAML.

namespace PocketCampus.Common
{
    public class LocalizedStrings
    {
        private static CommonResources _resources = new CommonResources();

        public CommonResources Resources
        {
            get { return _resources; }
        }
    }
}