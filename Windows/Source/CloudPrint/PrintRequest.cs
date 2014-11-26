using System;

namespace PocketCampus.CloudPrint
{
    public sealed class PrintRequest
    {
        public string DocumentName { get; private set; }
        public Uri FileUri { get; private set; }
        public long? DocumentId { get; private set; }

        public PrintRequest( string documentName, Uri fileUri )
        {
            DocumentName = documentName;
            FileUri = fileUri;
        }

        public PrintRequest( string documentName, long documentId )
        {
            DocumentName = documentName;
            DocumentId = documentId;
        }
    }
}