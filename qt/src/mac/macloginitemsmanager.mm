#include "macloginitemsmanager.h"
#import <Cocoa/Cocoa.h>

class MacLoginItemsManager::Private
{
public:
    Private(LSSharedFileListRef loginItems) : loginItems(loginItems) { }

    CFURLRef runningApplicationUrl() const
    {
        // This will retrieve the path for the application, i.e. /Applications/MyApplication.app
        NSString *appPath = [[NSBundle mainBundle] bundlePath];
        return reinterpret_cast<CFURLRef>([NSURL fileURLWithPath: appPath]);
    }

    LSSharedFileListRef loginItems;
};

MacLoginItemsManager::MacLoginItemsManager()
{
    // Create a reference to the shared file list
    // We are adding it to the current user only - if we wanted to add it all users,
    // use kLSSharedFileListGlobalLoginItems instead of kLSSharedFileListSessionLoginItems
    this->d = new MacLoginItemsManager::Private(LSSharedFileListCreate(NULL, kLSSharedFileListSessionLoginItems, NULL));
}

MacLoginItemsManager::~MacLoginItemsManager()
{
    if (this->d->loginItems)
    {
        CFRelease(this->d->loginItems);
    }

    delete this->d;
}

bool MacLoginItemsManager::appendRunningApplication() const
{
    if (this->d->loginItems)
    {
        CFMutableDictionaryRef inPropertiesToSet = CFDictionaryCreateMutable(NULL, 1, NULL, NULL);
        CFDictionaryAddValue(inPropertiesToSet, kLSSharedFileListLoginItemHidden, kCFBooleanTrue);

        // Add the running application to the list
        LSSharedFileListItemRef item = LSSharedFileListInsertItemURL(this->d->loginItems, kLSSharedFileListItemLast, NULL, NULL, this->d->runningApplicationUrl(), inPropertiesToSet, NULL);
        if (item)
        {
            CFRelease(item);
            return true;
        }
    }

    return false;
}

bool MacLoginItemsManager::removeRunningApplication() const
{
    return this->findRunningApplication(true);
}

bool MacLoginItemsManager::containsRunningApplication() const
{
    return this->findRunningApplication(false);
}

bool MacLoginItemsManager::findRunningApplication(bool remove) const
{
    bool success = false;

    if (this->d->loginItems)
    {
        UInt32 seedValue;

        // Retrieve the list of Login Items and cast them to an NSArray so that it will be easier to iterate
        NSArray *loginItemsArray = (NSArray*)LSSharedFileListCopySnapshot(this->d->loginItems, &seedValue);
        for (uint i = 0; i < [loginItemsArray count]; i++)
        {
            LSSharedFileListItemRef itemRef = reinterpret_cast<LSSharedFileListItemRef>([loginItemsArray objectAtIndex: i]);

            // Resolve the item with URL
            CFURLRef url = this->d->runningApplicationUrl();
            if (LSSharedFileListItemResolve(itemRef, 0, reinterpret_cast<CFURLRef*>(&url), NULL) == noErr)
            {
                NSString *urlPath = [reinterpret_cast<const NSURL*>(url) path];
                if ([urlPath compare: [[NSBundle mainBundle] bundlePath]] == NSOrderedSame)
                {
                    // If our goal is to remove it, our success is based on whether it's removed
                    if (remove)
                    {
                        success = (LSSharedFileListItemRemove(this->d->loginItems, itemRef) == noErr);
                    }
                    else
                    {
                        // Otherwise we just wanted to find it, and we did, so return true
                        return true;
                    }
                }
            }
        }

        [loginItemsArray release];
    }

    return success;
}
