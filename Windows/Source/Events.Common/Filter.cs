using PocketCampus.Mvvm;

namespace PocketCampus.Events
{
    public sealed class Filter<T> : ObservableObject
    {
        private bool _include;

        public string DisplayName { get; private set; }

        public T Id { get; private set; }

        public bool Include
        {
            get { return _include; }
            set { SetProperty( ref _include, value ); }
        }

        public Filter( string displayName, T id, bool include )
        {
            DisplayName = displayName;
            Id = id;
            Include = include;
        }
    }
}