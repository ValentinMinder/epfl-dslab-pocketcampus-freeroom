using System.Linq;
using PocketCampus.Events.Models;
using PocketCampus.Mvvm;

namespace PocketCampus.Events.ViewModels
{
    public sealed class TagFilterViewModel : ViewModel<EventPool>
    {
        private readonly IPluginSettings _settings;
        private readonly EventPool _pool;

        public Filter<string>[] Filters { get; private set; }

        public TagFilterViewModel( IPluginSettings settings,
                                   EventPool pool )
        {
            _settings = settings;
            _pool = pool;

            Filters = pool.Items
                          .Where( i => i.TagIds != null )
                          .SelectMany( i => i.TagIds )
                          .Select( id => new Filter<string>( _settings.EventTags[id], id, _settings.ExcludedTagsByPool[pool.Id].Contains( id ) ) )
                          .ToArray();
        }

        public override void OnNavigatedFrom()
        {
            var excluded = _settings.ExcludedTagsByPool;
            excluded[_pool.Id] = Filters.Where( t => !t.Include ).Select( t => t.Id ).ToArray();
            _settings.ExcludedTagsByPool = excluded;
        }
    }
}