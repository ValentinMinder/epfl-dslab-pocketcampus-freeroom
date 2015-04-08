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
    [LogId( "/events/tags" )]
    public sealed class TagFilterViewModel : ViewModel<EventPool>
    {
        private readonly IPluginSettings _settings;
        private readonly EventPool _pool;


        public Filter<string>[] Tags { get; private set; }


        public TagFilterViewModel( IPluginSettings settings,
                                   EventPool pool )
        {
            _settings = settings;
            _pool = pool;

            Tags = pool.Items
                       .Where( i => i.TagIds != null )
                       .SelectMany( i => i.TagIds )
                       .Distinct()
                       .Select( id => new Filter<string>( _settings.EventTags[id], id, !_settings.ExcludedTagsByPool[pool.Id].Contains( id ) ) )
                       .OrderBy( f => f.DisplayName )
                       .ToArray();
        }


        public override void OnNavigatedFrom()
        {
            var excluded = _settings.ExcludedTagsByPool;
            excluded[_pool.Id] = Tags.Where( t => !t.Include ).Select( t => t.Id ).ToArray();
            _settings.ExcludedTagsByPool = excluded;
        }
    }
}