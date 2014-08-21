// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using PocketCampus.Map.Models;
using ThinMvvm;

// Design data for MainViewModel

namespace PocketCampus.Map.ViewModels.Design
{
    public sealed class DesignMainViewModel
    {
#if DEBUG
        public DataStatus DataStatus { get { return DataStatus.DataLoaded; } }

        public MapProperties Properties
        {
            get { return new MapProperties { BuildingsLevel = 1, ZoomLevel = 16 }; }
        }

        public SearchProvider SearchProvider
        {
            get { return new SearchProvider( null ); }
        }

        public string SearchQuery
        {
            get { return "CO 1"; }
            set { }
        }

        public MapItem[] SearchResults
        {
            get
            {
                return new[]
                {
                    new MapItem
                    {
                        Name = "CO 1",
                        Floor = 1
                    },
                    new MapItem
                    {
                        Name = "CO 11",
                        Floor = 1
                    }
                };
            }
        }

        public bool AnySearchResults { get { return true; } }
#endif
    }
}