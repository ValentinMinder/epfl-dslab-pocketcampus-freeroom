// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

namespace PocketCampus.Events.ViewModels.Design
{
    public sealed class DesignCategoryFilterViewModel
    {
#if DEBUG
        public Filter<int>[] Categories
        {
            get
            {
                return new[]
                {
                    new Filter<int>( "Governing and Communicating Uncertainty", 0, true ),
                    new Filter<int>( "All", 0, true ),
                    new Filter<int>( "Governance of Energy Transition", 0, true ),
                    new Filter<int>( "Conferences - Seminars", 0, true ),
                    new Filter<int>( "Meetings management tips", 0, true ),
                    new Filter<int>( "Understanding and Managing Uncertainty", 0, true ),
                    new Filter<int>( "Miscellaneous", 0, true ),
                    new Filter<int>( "Exhibitions", 0, true ),
                    new Filter<int>( "Decision Making under Uncertainty: Behavioral Aspects", 0, true ),
                    new Filter<int>( "Movies", 0, true ),
                    new Filter<int>( "Risk Quantification", 0, true ),
                    new Filter<int>( "Celebrations", 0, true ),
                    new Filter<int>( "Inaugural lessons - Lessons of honor", 0, true ),
                    new Filter<int>( "Cultural events", 0, true ),
                    new Filter<int>( "Sporting events", 0, true ),
                    new Filter<int>( "Dating EPFL - economy", 0, true ),
                    new Filter<int>( "Thesis defenses", 0, true ),
                    new Filter<int>( "Academic calendar", 0, true ),
                    new Filter<int>( "Uncategorized", 0, true ),
                    new Filter<int>( "Me", 0, true ),
                    new Filter<int>( "Favorites", 0, true ),
                    new Filter<int>( "My events", 0, true ),
                    new Filter<int>( "First Day", 0, true ),
                    new Filter<int>( "Talks", 0, true ),
                    new Filter<int>( "Participants", 0, true ),
                    new Filter<int>( "Second Day", 0, true ),
                    new Filter<int>( "Schedule", 0, true ),
                    new Filter<int>( "Groups of Labs", 0, true ),
                    new Filter<int>( "Individual Labs", 0, true ),
                    new Filter<int>( "Professors", 0, true ),
                    new Filter<int>( "Poster Authors", 0, true ),
                    new Filter<int>( "Posters", 0, true ),
                    new Filter<int>( "Labs", 0, true ),
                    new Filter<int>( "Sponsoring Labs", 0, true ),
                    new Filter<int>( "Sponsors", 0, true ),
                    new Filter<int>( "Useful Information", 0, true ),
                    new Filter<int>( "Research Areas", 0, true ),
                    new Filter<int>( "Affiliates", 0, true ),
                    new Filter<int>( "Links", 0, true ),
                    new Filter<int>( "Venue", 0, true )
                };
            }
        }
#endif
    }
}