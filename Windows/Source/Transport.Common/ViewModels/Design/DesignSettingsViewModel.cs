// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

// Design data for SettingsViewModel

namespace PocketCampus.Transport.ViewModels.Design
{
    public sealed class DesignSettingsViewModel
    {
#if DEBUG
        public bool SortByPosition
        {
            get { return true; }
        }
#endif
    }
}