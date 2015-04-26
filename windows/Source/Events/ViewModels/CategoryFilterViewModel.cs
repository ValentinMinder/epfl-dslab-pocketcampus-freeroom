// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Linq;
using PocketCampus.Events.Models;
using PocketCampus.Events.Services;
using ThinMvvm;
using ThinMvvm.Logging;

namespace PocketCampus.Events.ViewModels
{
    [LogId( "/events/categories" )]
    public sealed class CategoryFilterViewModel : ViewModel<EventPool>
    {
        private readonly IPluginSettings _settings;
        private readonly EventPool _pool;


        public Filter<int>[] Categories { get; private set; }


        public CategoryFilterViewModel( IPluginSettings settings,
                                        EventPool pool )
        {
            _settings = settings;
            _pool = pool;

            Categories = pool.Items
                             .Where( i => i.CategoryId.HasValue )
                             .Select( i => i.CategoryId.Value )
                             .Distinct()
                             .Select( id => new Filter<int>( _settings.EventCategories.ContainsKey( id ) ? _settings.EventCategories[id] : "???",
                                                             id, !_settings.ExcludedCategoriesByPool[pool.Id].Contains( id ) ) )
                             .OrderBy( f => f.DisplayName )
                             .ToArray();
        }


        public override void OnNavigatedFrom()
        {
            var excluded = _settings.ExcludedCategoriesByPool;
            excluded[_pool.Id] = Categories.Where( t => !t.Include ).Select( t => t.Id ).ToArray();
            _settings.ExcludedCategoriesByPool = excluded;
        }
    }
}