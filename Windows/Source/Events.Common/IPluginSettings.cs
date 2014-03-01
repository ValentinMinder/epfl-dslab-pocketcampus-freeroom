using System.Collections.Generic;

namespace PocketCampus.Events
{
    public interface IPluginSettings
    {
        IList<string> UserTickets { get; set; }

        int SearchDayCount { get; set; }

        bool SearchInPast { get; set; }

        IDictionary<int, string> EventCategories { get; set; }

        IDictionary<string, string> EventTags { get; set; }

        IDictionary<long, IList<long>> FavoritesByPool { get; set; }

        IDictionary<long, IList<int>> ExcludedCategoriesByPool { get; set; }

        IDictionary<long, IList<string>> ExcludedTagsByPool { get; set; }
    }
}