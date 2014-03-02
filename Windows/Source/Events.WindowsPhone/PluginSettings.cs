using System.Collections.Generic;
using PocketCampus.Common;

namespace PocketCampus.Events
{
    public sealed class PluginSettings : SettingsBase, IPluginSettings
    {
        public IList<string> UserTickets
        {
            get { return Get<IList<string>>(); }
            set { Set( value ); }
        }

        public SearchPeriod SearchPeriod
        {
            get { return Get<SearchPeriod>(); }
            set { Set( value ); }
        }

        public bool SearchInPast
        {
            get { return Get<bool>(); }
            set { Set( value ); }
        }

        public IDictionary<int, string> EventCategories
        {
            get { return Get<IDictionary<int, string>>(); }
            set { Set( value ); }
        }

        public IDictionary<string, string> EventTags
        {
            get { return Get<IDictionary<string, string>>(); }
            set { Set( value ); }
        }

        public IDictionary<long, IList<long>> FavoritesByPool
        {
            get { return Get<IDictionary<long, IList<long>>>(); }
            set { Set( value ); }
        }

        public IDictionary<long, IList<int>> ExcludedCategoriesByPool
        {
            get { return Get<IDictionary<long, IList<int>>>(); }
            set { Set( value ); }
        }

        public IDictionary<long, IList<string>> ExcludedTagsByPool
        {
            get { return Get<IDictionary<long, IList<string>>>(); }
            set { Set( value ); }
        }

        public PluginSettings( IApplicationSettings applicationSettings ) : base( applicationSettings ) { }

        protected override SettingsDefaultValues GetDefaultValues()
        {
            return new SettingsDefaultValues<PluginSettings>
            {
                { x => x.UserTickets, () => new List<string>() },
                { x => x.SearchPeriod,() => SearchPeriod.OneMonth },
                { x => x.SearchInPast, () => false },
                { x => x.EventCategories, () => new Dictionary<int, string>() },
                { x => x.EventTags, () => new Dictionary<string, string>() },
                { x => x.FavoritesByPool, () => new Dictionary<long, IList<long>>() },
                { x => x.ExcludedCategoriesByPool, () => new Dictionary<long, IList<int>>() },
                { x => x.ExcludedTagsByPool, () => new Dictionary<long, IList<string>>() }
            };
        }
    }
}