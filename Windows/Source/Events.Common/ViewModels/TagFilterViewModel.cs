using System.Linq;
using PocketCampus.Events.Models;
using PocketCampus.Mvvm;

namespace PocketCampus.Events.ViewModels
{
    public sealed class TagFilterViewModel : ViewModel<EventPool>
    {
        private readonly IPluginSettings _settings;
        private readonly EventPool _pool;

        public TagFilter[] Filters { get; private set; }

        public TagFilterViewModel( IPluginSettings settings,
                                   EventPool pool )
        {
            _settings = settings;
            _pool = pool;

            Filters = pool.Items
                          .Where( i => i.TagIds != null )
                          .SelectMany( i => i.TagIds )
                          .Select( id => new TagFilter( _settings.EventTags[id], id, _settings.ExcludedTagsByPool[pool.Id].Contains( id ) ) )
                          .ToArray();
        }

        public override void OnNavigatedFrom()
        {
            var excluded = _settings.ExcludedTagsByPool;
            excluded[_pool.Id] = Filters.Where( t => !t.Include ).Select( t => t.Id ).ToArray();
            _settings.ExcludedTagsByPool = excluded;
        }


        public sealed class TagFilter : ObservableObject
        {
            private bool _include;

            public string DisplayName { get; private set; }

            public string Id { get; private set; }

            public bool Include
            {
                get { return _include; }
                set { SetProperty( ref _include, value ); }
            }

            public TagFilter( string displayName, string id, bool include )
            {
                DisplayName = displayName;
                Id = id;
                Include = include;
            }
        }
    }
}