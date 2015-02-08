// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;

namespace PocketCampus.CloudPrint
{
    public sealed class PrintRequest
    {
        public string DocumentName { get; private set; }
        public Uri FileUri { get; private set; }
        public long? DocumentId { get; private set; }
        public PrintSettings Settings { get; private set; }

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

        public PrintRequest( PrintRequest original, PrintSettings settings )
        {
            DocumentName = original.DocumentName;
            FileUri = original.FileUri;
            DocumentId = original.DocumentId;
            Settings = settings;
        }
    }
}