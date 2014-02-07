// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

// Design data for AddStationViewModel

namespace PocketCampus.Transport.ViewModels.Design
{
    public sealed class DesignAddStationViewModel
    {
#if DEBUG
        public bool IsLoading { get { return false; } }
        public bool HasError { get { return false; } }
        public bool HasNetworkError { get { return false; } }
#endif
    }
}