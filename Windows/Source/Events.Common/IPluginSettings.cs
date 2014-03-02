using System.Collections.Generic;

namespace PocketCampus.Events
{
    public interface IPluginSettings
    {
        List<string> UserTickets { get; set; }

        SearchPeriod SearchPeriod { get; set; }

        bool SearchInPast { get; set; }

        Dictionary<int, string> EventCategories { get; set; }

        Dictionary<string, string> EventTags { get; set; }

        List<long> FavoriteEventIds { get; set; }

        Dictionary<long, List<int>> ExcludedCategoriesByPool { get; set; }

        Dictionary<long, List<string>> ExcludedTagsByPool { get; set; }
    }
}