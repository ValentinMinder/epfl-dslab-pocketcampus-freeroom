using System.Linq;
using PocketCampus.Events.Models;
using PocketCampus.Mvvm;

namespace PocketCampus.Events.ViewModels
{
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
                             .Select( id => new Filter<int>( _settings.EventCategories[id], id, _settings.ExcludedCategoriesByPool[pool.Id].Contains( id ) ) )
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