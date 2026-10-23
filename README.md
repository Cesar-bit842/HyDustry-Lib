<p><img src="https://media.forgecdn.net/attachments/description/null/description_e2028ba1-c9d8-4a44-9dab-3a2d9e383f93.png" alt="" width="849" height="297"></p>
<h3><strong>HyDustry Lib</strong></h3>
<p><em>The <strong>Open Source</strong> Library for Java and for the Node Editor -&gt; <a href="https://github.com/Cesar-bit842/HyDustry-Lib" target="_blank" rel="nofollow noopener">Open Source [Github]</a></em></p>
<p>&nbsp;</p>
<h1><span style="color: #e03e2d;"><strong><em>for Java programmers</em></strong></span></h1>
<p><span style="color: #3598db;"><em>class HyDustryLib&nbsp;</em></span></p>
<h5><span style="color: #f1c40f;"><strong><em>HyDustryLib.AddConsumerMachineOperationType(String Name, Consumer&lt;ConsumerMachineComponent&gt; Function):</em></strong></span></h5>
<h4><span style="color: #3598db;"><em>Add a new operation format to the machines.&nbsp;</em></span></h4>
<h4><span style="color: #3598db;"><em>example:&nbsp;</em></span></h4>
<div>
<h5><span style="color: #843fa1;"><code>HyDustryLib.AddConsumerMachineOperationType("Essence Extractor", &lt;classname&gt;::Execute);</code></span></h5>
</div>
<h5><span style="color: #3598db;"><em><br><span style="color: #f1c40f;"><strong>HyDustryLib.AddConveyorOperationType(String Name, Consumer&lt;ConveyorTransporterComponent&gt; Function):&nbsp;</strong></span></em></span></h5>
<h4><span style="color: #3598db;"><em>agrega o reemplaza en el&nbsp;mapa una funcion de operacion para transportadores.</em></span></h4>
<h4><span style="color: #3598db;"><em>example:&nbsp;</em></span></h4>
<div>
<div>
<h5><span style="color: #843fa1;"><code>HyDustryLib.AddConveyorOperationType("Rotator Conveyor", &lt;classname&gt;::Execute);</code></span></h5>
</div>
</div>
<p>&nbsp;</p>
<h5><span style="color: #f1c40f;"><strong><em>HyDustryLib.get()</em></strong></span></h5>
<h4><span style="color: #3598db;"><em>Add a new operation format to the machines.&nbsp;</em></span></h4>
<h4><span style="color: #3598db;"><em>example:&nbsp;&nbsp;</em></span></h4>
<p>&nbsp;</p>
<div>
<h5><span style="color: #f1c40f;"><strong><em>HyDustryLib.getConsumerMachineComponentType()</em></strong></span></h5>
<h4><span style="color: #3598db;"><em>returns the registered type of getConsumerMachineComponentType.</em></span></h4>
<p>&nbsp;</p>
<h5><span style="color: #f1c40f;"><strong><em>HyDustryLib.getConveyorTransporterComponentType()</em></strong></span></h5>
<h4><span style="color: #3598db;"><em>returns the registered type of getConveyorTransporterComponentType.</em></span></h4>
<div>
<p>&nbsp;</p>
<h5><span style="color: #f1c40f;"><strong><em>HyDustryLib.getHarvestingEngineComponentType()</em></strong></span></h5>
<h4><span style="color: #3598db;"><em>returns the registered type of getHarvestingEngineComponentType.</em></span></h4>
</div>
</div>
<p>&nbsp;</p>
<h3><span style="color: #e03e2d;"><strong><em>Main components</em></strong></span></h3>
<h4><strong>ConsumerMachineComponent</strong></h4>
<p>Represents a machine that consumes materials and generates products. Its configurable fields include <code>ManufacturingTime</code>, <code>RequireFuel</code>, <code>RequireEnergy</code>, <code>InputItems</code>, <code>OutputItems</code>, and <code>OperationType</code>.</p>
<ul>
<li><code>ExecuteBehaviour()</code>: Executes the operation registered for <code>OperationType</code>; uses the default behavior if no operation is found.</li>
<li><code>ExecuteBehaviourDefault()</code>: Validates the time, materials, fuel, and containers; consumes the resources and delivers the configured products when space is available.</li>
<li><code>InitializeConfiguration(Map&lt;String, String[]&gt;)</code>: Configures the operation type and stores the block's tags within the component.</li>
<li><code>GetItemsOutput()</code>: Constructs the list of output products based on the machine's configuration.</li>
<li><code>ItemsVerification(boolean)</code>: Checks whether the required materials are available.</li>
<li><code>processItems(boolean)</code>: Consumes the materials required by the recipe or the active upgrade.</li>
<li><code>getAvailableFuelId()</code> and <code>processFuel(String)</code>: Check for and consume the configured fuel.</li>
<li><code>ManufacturingAvailability()</code> and <code>RenewManufacturingAvailability(Long)</code>: Check and update the manufacturing cooldown/wait time.</li>
<li><code>GetInputItemsHyDustryComponent()</code> and <code>GetOutputItemsHyDustryComponent()</code>: Return the component's input and output containers.</li>
</ul>
<h4><strong>ConveyorTransporterComponent</strong></h4>
<p>Represents the state of a conveyor. <code>Velocity</code> controls forward movement; <code>Progress</code> indicates the completion status of a task; <code>ItemsDetected</code> and <code>LookingBehaviour</code> indicate object detection and the current search state.</p>
<h4><strong>HarvestingEngineComponent</strong></h4>
<p>Represents a harvesting engine. It stores <code>Range</code>, <code>Interval</code>, the current phase, irrigation timing, and the following triggered actions: <code>Water</code>, <code>Harvest</code>, <code>Fertilize</code>, and <code>Accelerate</code>.</p>
<p><span style="color: #3598db;"><em>Example operation: Extractor</em></span></p>
<h4><strong>Extractor.Execute(ConsumerMachineComponent)</strong></h4>
<p>An operation included for consumer machines. Scans the configured positions, identifies the predominant resource, calculates the output and its multipliers, consumes the required materials, and stores the products if the output container has space.</p>
<pre><code>HyDustryLib.AddConsumerMachineOperationType("Essence Extractor", Extractor::Execute);</code></pre>
<p><span style="color: #3598db;"><em>Implemented Utilities</em></span></p>
<h4><strong>EssentialsUtils</strong></h4>
<ul>
<li><code>getBlockType(World, Vector3i)</code>: queries the block type at a given position.</li>
<li><code>getBlockSection(World, Vector3i)</code>: retrieves the section containing the position.</li>
<li><code>getBlockRef(World, Vector3i)</code>: retrieves the block's entity reference.</li>
<li><code>getBlockRotation(World, Vector3i)</code>: retrieves the block's rotation.</li>
<li><code>getGlobalPosition(...)</code>: converts a block's local coordinates into world coordinates.</li>
</ul>
<h4><strong>RailsHyDustryLib</strong></h4>
<ul>
<li><code>OrientRails(Vector3d[], int)</code>: rotates waypoints to align with the block's orientation.</li>
<li><code>OrientGlobalPositions(Vector3d[], int)</code>: transforms offsets relative to the block's orientation.</li>
<li><code>RailsCalculateNextPosition(Vector3d[], double, int)</code>: calculates a position along a path based on progress and smoothing.</li>
<li><code>TagsPathsConversion(Map)</code>: converts path tags into vectors.</li>
<li><code>ConvertingTagstoArrays(Map, String)</code> and <code>ConvertingTagsPositionstoArrays(Map, String)</code>: convert coordinate tags into arrays of vectors.</li>
</ul>
<h4><strong>FarmingHyDustryLib</strong></h4>
<ul>
<li><code>ProccesingCoopsInRange(...)</code>: processes the blocks within the engine's range and applies any enabled care and harvesting actions.</li>
<li><code>AccelerateGrowthInBlock(...)</code>: advances the crop toward the next stage of growth.</li>
<li><code>WateringInBlock(...)</code>: extends the irrigation duration in a block. tilled soil.</li>
<li><code>WateringAndFetilizedInBlock(...)</code>: waters and fertilizes the tilled soil.</li>
<li><code>FetilizedInBlock(...)</code>: fertilizes the tilled soil.</li>
<li><code>UpdateGrowthInBlock(...)</code>: schedules a growth update for the block.</li>
</ul>
<h4><strong>HarvestingHyDustryLib</strong></h4>
<ul>
<li><code>HarvestInSimpleContainer(...)</code>: collects drops from a block and updates the block or the crop's growth stage.</li>
<li><code>HarvestValidationGatheringInContainer(...)</code>: checks whether the drops can be stored in the container.</li>
<li><code>HarvestBlockValidation(...)</code>: checks whether the block has a valid harvest configuration.</li>
</ul>
<h4><strong>Item Containers</strong></h4>
<p><code>ItemContainerHyDustryLib</code> combines item-container and simple-container helpers: it can locate an item container at an absolute or configured relative position, check whether an item fits, retrieve the last occupied stack, and remove a full stack or a requested quantity.</p>
<p><em>Practical guide to the utilities in the Lib folder</em></p>
<h3><strong><span style="color: #e03e2d;"><em>Before using these functions</em></span></strong></h3>
<p><span style="color: #e03e2d;"><em>This guide covers the utility classes in <code>src/main/java/Hytale/Modification/hydustrylib/Lib</code>. Check for a valid block reference and component before using a lookup result, and run operations that modify the world or inventories in the appropriate server context.</em></span></p>
<h3><strong><span style="color: #3598db;"><em>Getting started</em></span></strong></h3>
<p>The classes in <code>Lib</code> primarily serve as containers for static methods. They are invoked using the class name; there is no need to create an instance. The examples assume that your mod retrieves the <code>World</code>, <code>CommandBuffer</code>, and block references from the appropriate server context.</p>
<pre><code>import Hytale.Modification.hydustrylib.Lib.EssentialsUtils;
import Hytale.Modification.hydustrylib.Lib.ItemContainerHyDustryLib;

BlockType type = EssentialsUtils.getBlockType(world, blockPosition);
ItemContainerBlock input =
ItemContainerHyDustryLib.GetItemContainerComponent(world, blockPosition);

if (type != null &amp;&amp; input != null) {
// Use the block and its inventory within the server context.
}</code></pre>
<p>Several queries will return <code>null</code> if the block, its section, or its component does not exist. Check these results before accessing them. Functions that modify blocks, inventories, or entities must be executed within the appropriate context of the world and its stores.</p>
<h3><strong><span style="color: #3598db;"><em>Block and Coordinate Utilities</em></span></strong></h3>
<h4><strong>EssentialsUtils</strong></h4>
<p>Low-level queries for blocks and position conversion. The <code>Vector3i</code> type represents integer block coordinates; the result of <code>getGlobalPosition</code> is a world position.</p>
<ul>
<li><code>getBlockType(World world, Vector3i Position)</code>: returns the <code>BlockType</code> at the position, or <code>null</code> if it could not be resolved.</li>
<li><code>getBlockSection(World world, Vector3i Position)</code>: returns the <code>BlockSection</code> containing the position, or <code>null</code>.</li>
<li><code>getBlockRef(World world, Vector3i Position)</code>: returns the entity reference associated with the block, or <code>null</code>.</li>
<li><code>getBlockRotation(World world, Vector3i Position)</code>: returns the rotation index; returns <code>-1</code> if the section cannot be retrieved.</li>
<li><code>getGlobalPosition(BlockStateInfo, CommandBuffer&lt;ChunkStore&gt;)</code>: calculates the world position using the block state's index and section.</li>
<li><code>getGlobalPosition(Vector3ic localPosition, BlockStateInfo,
CommandBuffer&lt;ChunkStore&gt;)</code>: converts an explicit local position; returns <code>null</code> if the section cannot be retrieved.</li>
<li><code>GetComponentType(World, Vector3i, ComponentType)</code>: retrieves the requested component from the block at an absolute position, or <code>null</code> if the block reference is unavailable.</li>
<li><code>getRelativePosition(World, Vector3i, Vector3d)</code>: rotates a block-relative offset and resolves filler blocks; honors the <code>HyDustryUtility.Vertical</code> tag.</li>
<li><code>getValidRelativePosition(World, Vector3i, Vector3d)</code>: resolves a relative position in the same way and returns it only when a block with data exists there.</li>
</ul>
<p><strong>Example: querying type and rotation</strong></p>
<pre><code>BlockType type = EssentialsUtils.getBlockType(world, position);
int rotation = EssentialsUtils.getBlockRotation(world, position); if (type != null &amp;&amp; rotation &gt;= 0) {
Map&lt;String, String[]&gt; tags = type.getData().getRawTags();
}</code></pre>
<p>The last line requires that <code>type.getData()</code> not be <code>null</code>. A coordinate outside of a loaded section may not yield a valid reference or result.</p>
<h4><strong>FillerBlocksHyDustryLib</strong></h4>
<ul>
<li><code>CalculateRealPositionUsingFillerBlocks(World world,
Vector3i Position)</code>: if the position belongs to a filler block, it attempts to return the position of the main solid block. If it cannot be resolved, it returns the original position.</li>
</ul>
<p>It is advisable to apply this function before querying a block that might be part of a multi-block structure.</p>
<pre><code>Vector3i realPosition =
FillerBlocksHyDustryLib.CalculateRealPositionUsingFillerBlocks(world, hitPosition);
BlockType type = EssentialsUtils.getBlockType(world, realPosition);</code></pre>
<h4><strong>RailsHyDustryLib: orientation and movement</strong></h4>
<ul>
<li><code>OrientRails(Vector3d[] points, int direction)</code>: returns a copy of the points rotated around the horizontal center of the block. Horizontal direction indices are mapped to four orientations; some indices between 8 and 63 also invert the Y-coordinate.</li>
<li><code>OrientGlobalPositions(Vector3d[] points, ​​int direction)</code>: rotates relative displacements. Used for transforming offsets around a block, not for orienting absolute world-space points.</li>
<li><code>RailsCalculateNextPosition(Vector3d[] rotatedPoints,
double progressPercent, int smoothing)</code>: interpolates between the first three points. Progress is clamped to 0&ndash;100; smoothing is clamped to 0&ndash;10. If fewer than three points are provided, it returns the center <code>(0.5, 0.5, 0.5)</code>.</li>
<li><code>TagsPathsConversion(Map&lt;String, String[]&gt; Tags)</code>: converts the <code>HyDustryUtility.Paths</code> tag into vectors. If the tag is missing or empty, it returns a single center point.</li>
<li><code>ConvertingTagstoArrays(Map, String Tag)</code>: reads the <code>HyDustryUtility.&lt;Tag&gt;</code> tag as comma-separated coordinates (e.g., <code>1,0,0</code>). Returns <code>null</code> if no values ​​are present.</li>
<li><code>ConvertingTagsPositionstoArrays(Map, String Tag)</code>: equivalent to the above, but handles coordinates that may be enclosed in parentheses.</li>
<li><code>vectorArrayToString(Vector3d[] vectors)</code>: converts an array into a text format with coordinate triplets enclosed in parentheses; a <code>null</code> or empty array is converted to <code>[]</code>.</li>
</ul>
<p><strong>Example: obtaining an object's position along a path</strong></p>
<pre><code>Map&lt;String, String[]&gt; tags = blockType.getData().getRawTags();
Vector3d[] path = RailsHyDustryLib.TagsPathsConversion(tags);
Vector3d[] orientedPath = RailsHyDustryLib.OrientRails(path, rotation);
Vector3d pointOnBlock =
RailsHyDustryLib.RailsCalculateNextPosition(orientedPath, progressPercent, 0);

Vector3d worldPosition = new Vector3d(position.x, position.y, position.z)
.add(pointOnBlock.x, pointOnBlock.y, pointOnBlock.z);</code></pre>
<p>The path must contain at least three points for interpolation. For offsets relative to the machine, use <code>OrientGlobalPositions</code> instead of <code>OrientRails</code>; both functions apply different conventions.</p>
<h3><strong><span style="color: #3598db;"><em>Utilities for finding block components</em></span></strong></h3>
<p><code>EssentialsUtils</code> provides generic component lookup at an absolute position and helpers for rotating relative offsets, resolving filler blocks, and checking that a destination block exists. Relative lookups honor the <code>HyDustryUtility.Vertical</code> tag.</p>
<h4><strong>ItemContainerHyDustryLib</strong></h4>
<ul>
<li><code>GetItemContainerComponent(World, Vector3i)</code>: retrieves an <code>ItemContainerBlock</code> at an absolute position.</li>
<li><code>GetItemContainerComponent(World, Vector3i, Vector3d)</code>: resolves the relative offset from a base block, including rotation and filler blocks, then retrieves the container.</li>
<li><code>ValidationItemForAdding(ItemContainerBlock, ItemStack)</code> and <code>ValidationItemForAdding(SimpleItemContainer, ItemStack)</code>: check for an empty slot or a compatible stack with room.</li>
<li><code>getLastItem(ItemContainerBlock)</code>: returns the last occupied stack in a block container.</li>
<li><code>getLastItem(SimpleItemContainer, int count)</code>: returns a copy of up to <code>count</code> items from the last occupied stack without changing the container.</li>
<li><code>removeLastItem(ItemContainerBlock)</code>: removes the last occupied stack from a block container.</li>
<li><code>removeLastItem(SimpleItemContainer, int count)</code>: removes up to <code>count</code> items from the last occupied stack.</li>
</ul>
<h4><strong>ConsumerMachineHyDustryLib</strong></h4>
<ul>
<li><code>ConsumerMachineValidatorComponent(World, int,
Vector3i, Map&lt;String, String[]&gt;, Vector3d)</code>: searches for a consumer machine at the relative position and returns its <code>ConsumerMachineComponent</code>, or null if it does not exist.</li>
</ul>
<p>Lookups return <code>null</code> when the requested block or component cannot be resolved. They do not create or add components. Supply a base position and relative offset for relative lookups.</p>
<p><strong>Example: retrieving a relative container</strong></p>
<pre><code>ItemContainerBlock input =
ItemContainerHyDustryLib.GetItemContainerComponent(
world,
machinePosition,
new Vector3d(0, 0, -1)
);

if (input != null &amp;&amp; input.getItemContainer() != null) {
// Query or modify the inventory within a valid server context.
}</code></pre>
<h3><strong><span style="color: #3598db;"><em>Inventories</em></span></strong></h3>
<p><strong>Example: removing one unit without modifying it before inspection</strong></p>
<pre><code>ItemStack preview =
ItemContainerHyDustryLib.getLastItem(container, 1);

if (preview != null &amp;&amp; isValidDestination(preview)) {
boolean removed =
ItemContainerHyDustryLib.removeLastItem(container, 1); 
if (removed) {
// Transfer preview to the destination. 
}
}</code></pre>
<p><code>ValidationItemForAdding</code> only checks whether there is capacity; it does not add the object or reserve space. Between validation and addition, re-verify the state if other operations might modify the inventory.</p>
<h3><strong><span style="color: #3598db;"><em>Object Entities</em></span></strong></h3>
<h4><strong>EntityHyDustryLib</strong></h4>
<ul>
<li><code>EntityIsValid(Ref&lt;EntityStore&gt; ref)</code>: checks that the reference is valid, has a Store, and possesses a <code>UUIDComponent</code>.</li>
<li><code>ToggleBehaviourInteractionItems(TransformComponent transform,
boolean BH, Store&lt;EntityStore&gt; store, Ref&lt;EntityStore&gt; ref)</code>: if <code>BH == false</code>, it adds components that prevent merging and pickup; if <code>BH == true</code>, it removes them and adjusts the object's position.</li>
</ul>
<p>This function directly alters the entity's components and transform. Call it only when it is appropriate to temporarily change the object's behavior.</p>
<h3><strong><span style="color: #3598db;"><em>Farming and Harvesting</em></span></strong></h3>
<h4><strong>FarmingHyDustryLib</strong></h4>
<ul>
<li><code>ProccesingCoopsInRange(CommandBuffer&lt;ChunkStore&gt;,
HarvestingEngineComponent, Vector3i, int range, int Phase)</code>: iterates through the area around the engine and applies irrigation, fertilizer, growth acceleration, or harvesting, depending on the component's settings.</li>
<li><code>AccelerateGrowthInBlock(CommandBuffer&lt;ChunkStore&gt;,
Ref&lt;ChunkStore&gt;, World, BlockType)</code>: advances the crop's progress toward the next stage.</li>
<li><code>WateringInBlock(CommandBuffer&lt;ChunkStore&gt;,
Ref&lt;ChunkStore&gt;, World, float WateringTime)</code>: extends the irrigation duration and delays the degradation of tilled soil.</li>
<li><code>WateringAndFertilizedInBlock(...)</code>: performs irrigation and also marks the soil as fertilized.</li>
<li><code>FertilizedInBlock(CommandBuffer&lt;ChunkStore&gt;,
Ref&lt;ChunkStore&gt;)</code>: marks tilled soil as fertilized.</li>
<li><code>UpdateGrowthInBlock(CommandBuffer&lt;ChunkStore&gt;,
Ref&lt;ChunkStore&gt;, World)</code>: schedules an update for the crop component.</li>
<li><code>AccelerateCropGrowth(Vector3i, World)</code>: a variant that directly retrieves block references and advances growth to the next stage; it is marked <code>@Insecure</code>.</li>
<li><code>ScheduleInstantUpdate(CommandBuffer&lt;ChunkStore&gt;,
Ref&lt;ChunkStore&gt;, World)</code>: schedules a new tick for the block; it is marked <code>@Insecure</code>.</li>
</ul>
<p>Functions marked <code>@Insecure</code> require special care regarding block references and components. Prefer the variants that accept a <code>CommandBuffer</code> when the system already has one available.</p>
<h4><strong>HarvestingHyDustryLib</strong></h4>
<ul>
<li><code>HarvestInSimpleContainer(Vector3i blockPosition, World world,
ItemContainerBlock container)</code>: retrieves harvest drops and adds them to the inventory; it may reset a crop to its subsequent growth stage or replace the harvested block with air. Returns a boolean indicating the outcome of the harvest operation.</li>
<li><code>HarvestValidationGatheringInContainer(Vector3i, World,
ItemContainerBlock)</code>: checks the harvest configuration and whether the inventory can accept any of the drops.</li>
<li><code>HarvestBlockValidation(Vector3i, World)</code>: checks whether the block has a drop configuration and if its crop configuration allows it to be processed.</li>
</ul>
<p>Before harvesting, check the block type and the inventory. The harvest function may modify both the world and the container; do not use it as a read-only query.</p>
<h3><strong><span style="color: #3598db;"><em>API Annotations</em></span></strong></h3>
<ul>
<li><code>@safe(reason = "...")</code>: a source-level annotation used to identify methods or constructors considered stable by the library.</li>
<li><code>@Insecure(reason = "...")</code>: a source-level annotation used to warn that a method or constructor requires caution.</li>
</ul>
<p>These annotations do not replace runtime validation, nor do they automatically apply any protections. Their retention is <code>SOURCE</code>, meaning they are not preserved as metadata at runtime.</p>
<h1><span style="color: #e03e2d;"><strong><em>for Node Editor programmers</em></strong></span></h1>
<p>&nbsp;</p>
<p><em>Guide for configuring blocks, components, and tags using the Hytale Node Editor</em></p>
<h3><strong><span style="color: #e03e2d;"><em>Scope and Warnings</em></span></strong></h3>
<p><span style="color: #e03e2d;"><em> This guide outlines the keys and fields that are read or registered by the current code in HyDustry Lib. Tags represent block data, while components are distinct data elements added to a block via the editor. Not all tags are required for every block. Any keys not mentioned here are not currently used in the reviewed code. </em></span></p>
<p><strong>Important:</strong> The current code looks for keys that begin with <code>HyDustryUtility.</code>. A key named <code>HyDustry.Behaviour</code> is not equivalent to <code>HyDustryUtility.Behaviour</code> and will not trigger the conveyor behavior described in this guide.</p>
<h3><strong><span style="color: #3598db;"><em>How a Tag Is Represented</em></span></strong></h3>
<p>HyDustry Lib receives block tags as a <code>Map&lt;String, String[]&gt;</code>: each entry consists of a text key mapped to an array of text values. In Hytale source files, the format looks like this:</p>
<pre><code>{
"HyDustryUtility.Machine.Behaviour": ["Essence Extractor"],
"HyDustryUtility.Machine.Scanner.Positions": ["0,0,-1", "1,0,-1"]
}</code></pre>
<ul>
<li>Tag names and all elements within their arrays are strings, even if they represent numbers or booleans.</li>
<li>The order of values ​​matters in parallel lists: the element at index 0 corresponds to the value at index 0 of its associated tag.</li>
<li>Coordinates are written as <code>x,y,z</code>. Use a decimal point for fractions&mdash;for example, <code>0.5,0.5,1.0</code>.</li>
<li>Do not include comments within the JSON. Snippets in this guide containing <code>//</code> are for explanation only and are not part of the data.</li>
<li>A "flag" type tag can be activated simply by being present, regardless of the text it contains. For these, using <code>["true"]</code> is recommended.</li>
</ul>
<h3><strong><span style="color: #3598db;"><em>Rule of thumb: Tags vs. Components</em></span></strong></h3>
<p><strong>Tags:</strong> Describe configuration that the code reads from a block's raw tags. Examples include recipes, scanner positions, paths, and input/output offsets.</p>
<p><strong>Components:</strong> Store the state and options for HyDustry Lib systems. The plugin registers three components: <code>ConsumerMachineComponent</code>, <code>ConveyorTransporterComponent</code>, and <code>HarvestingEngineComponent</code>. Add these to the appropriate block via the editor and configure their fields in the component panel. Do not convert these fields into tags, unless the code explicitly indicates that it reads a tag.</p>
<p>The component examples below show the editable fields registered by the codec, not the entire Hytale block file wrapper. The exact way in which the Node Editor serializes or references components depends on the Hytale block format and should not be inferred from the tag map.</p>
<h3><strong><span style="color: #3598db;"><em>Summary of recognized tags</em></span></strong></h3>
<table>
<thead>
<tr>
<th>Full tag</th>
<th>Usage</th>
<th>Expected type</th>
</tr>
</thead>
<tbody>
<tr>
<td><code>HyDustryUtility.Machine.Behaviour</code></td>
<td>Name of the machine operation, for example <code>Essence Extractor</code>.</td>
<td>A name within an array of strings.</td>
</tr>
<tr>
<td><code>HyDustryUtility.Machine.Output.Items</code> / <code>HyDustryUtility.Machine.Output.Counts</code></td>
<td>Direct products of the default machine behavior.</td>
<td>Parallel lists: item IDs and integer quantities.</td>
</tr>
<tr>
<td><code>HyDustryUtility.Machine.Requiere.Items</code> / <code>HyDustryUtility.Machine.Requiere.Counts</code></td>
<td>Required ingredients. The code literally uses <code>Requiere</code>, with that exact spelling.</td>
<td>Parallel lists: IDs and integer quantities.</td>
</tr>
<tr>
<td><code>HyDustryUtility.Machine.Fuel.Items</code> / <code>HyDustryUtility.Machine.Fuel.Counts</code></td>
<td>Available fuels and the quantity of each.</td>
<td>Parallel lists: IDs and integer quantities.</td>
</tr>
<tr>
<td><code>HyDustryUtility.Machine.Fuel.Upgrades.N.*</code></td>
<td>Ingredients and production time for upgrade level N.</td>
<td>See the "Fuel and Upgrades" section.</td>
</tr>
<tr>
<td><code>HyDustryUtility.Machine.Scanner.Require.Items</code></td>
<td>Its presence triggers the verification and consumption of ingredients in the <code>Extractor</code>.</td>
<td>A flag; the content is not read.</td>
</tr>
<tr>
<td><code>HyDustryUtility.Machine.Scanner.Positions</code></td>
<td>Relative offsets of the blocks to be scanned by the extractor.</td>
<td>A list of coordinate strings.</td>
</tr>
<tr>
<td><code>HyDustryUtility.Machine.Scanner.Blocks</code></td>
<td>An ordered list of drop IDs recognized by the extractor. The index determines the output group.</td>
<td>A list of ID strings.</td>
</tr>
<tr>
<td><code>HyDustryUtility.Machine.Output.Items.N</code> / <code>HyDustryUtility.Machine.Output.Counts.N</code></td>
<td>The output recipe associated with index N of <code>Scanner.Blocks</code>.</td>
<td>Parallel lists: item IDs and integer quantities.</td>
</tr>
<tr>
<td><code>HyDustryUtility.Machine.Output.Items</code> / <code>HyDustryUtility.Machine.Output.Multiplicator</code></td>
<td>In the extractor, this links the detected ID to its multiplier.</td>
<td>Parallel lists: resource IDs and decimal multipliers.</td>
</tr>
<tr>
<td><code>HyDustryUtility.Machine.Output.Multiplicator.Timer.ID</code></td>
<td>Time values ​​indexed by the quantity detected for the specified ID.</td>
<td>An array of millisecond values ​​(as strings); see the note regarding indexing.</td>
</tr>
<tr>
<td><code>HyDustryUtility.Machine.Output.Timer.N</code></td>
<td>A key read by an auxiliary helper for index N.</td>
<td>An integer (as a string); currently not used by <code>Extractor.Execute</code>.</td>
</tr>
<tr>
<td><code>HyDustryUtility.Machine.Fuel.Scanner.Upgrades.N.Items</code> / <code>...Multiplicator</code></td>
<td>Scanner multipliers unlocked via a fuel upgrade.</td>
<td>Parallel lists of IDs and decimal values.</td>
</tr>
<tr>
<td><code>HyDustryUtility.Paths</code></td>
<td>Local path points followed by objects on a conveyor belt.</td>
<td>Ordered list of coordinate strings.</td>
</tr>
<tr>
<td><code>HyDustryUtility.Behaviour</code></td>
<td>A flag that allows the listener to treat the block as a conveyor belt surface; the value itself is not read.</td>
<td>Presence-based flag.</td>
</tr>
<tr>
<td><code>HyDustryUtility.Behaviour.Loocking</code></td>
<td>Activates a continuous search state for the conveyor belt when an object touches it. Note: the key is spelled <code>Loocking</code> in the code.</td>
<td>Presence-based flag.</td>
</tr>
<tr>
<td><code>HyDustryUtility.Items.Load.Positions</code></td>
<td>Relative offsets where the conveyor belt searches for containers to pick up items.</td>
<td>List of coordinate strings.</td>
</tr>
<tr>
<td><code>HyDustryUtility.Items.Save.Positions</code></td>
<td>Relative offsets where the conveyor belt attempts to deposit an item upon completing its path.</td>
<td>List of coordinate strings.</td>
</tr>
<tr>
<td><code>HyDustryUtility.Vertical</code></td>
<td>Instructs neighboring block validators to use a vertical offset instead of the expected horizontal displacement.</td>
<td>Presence-based flag; the value is not read.</td>
</tr>
</tbody>
</table>
<h3><strong><span style="color: #3598db;"><em>ConsumerMachineComponent</em></span></strong></h3>
<p>Add this component to the block representing a processing machine. The input and output containers are references/data from <code>ItemContainerBlock</code> that must be configured in the component's fields via the Node Editor; they are not object IDs within the tags.</p>
<table>
<thead>
<tr>
<th>Component field</th>
<th>Type</th>
<th>Current usage</th>
</tr>
</thead>
<tbody>
<tr>
<td><code>Manufacturing time (1s -&gt; 1000mls)</code></td>
<td>long</td>
<td>Base manufacturing time in milliseconds.</td>
</tr>
<tr>
<td><code>It requires energy</code></td>
<td>boolean</td>
<td>Stored, but the current implementation does not verify an energy source.</td>
</tr>
<tr>
<td><code>It requires fuel</code></td>
<td>boolean</td>
<td>If true, the code searches for and consumes the configured fuel.</td>
</tr>
<tr>
<td><code>Type of operation</code></td>
<td>string</td>
<td>Operation name; must match a registered operation. The plugin registers <code>Essence Extractor</code>.</td>
</tr>
<tr>
<td><code>InputItemsContainer</code></td>
<td>ItemContainerBlock</td>
<td>Container that provides ingredients and fuel.</td>
</tr>
<tr>
<td><code>OutputItemsContainer</code></td>
<td>ItemContainerBlock</td>
<td>Container that receives the products.</td>
</tr>
<tr>
<td><code>Position by single check [PBSC]</code></td>
<td>string</td>
<td>Internal positioning field; updated by the system.</td>
</tr>
<tr>
<td><code>SystemRaw [System]</code></td>
<td>string</td>
<td>Internal serialization of raw tags; initialized by the listener.</td>
</tr>
<tr>
<td><code>LastManufacture [System]</code></td>
<td>string</td>
<td>Internal timestamp for the cooldown period. Do not configure manually.</td>
</tr>
</tbody>
</table>
<h4><strong>1. Machine with default behavior</strong></h4>
<p>Set the operation to <code>Default</code> and add the tags for products and requirements. Quantities that are missing or cannot be read are interpreted as 1. This behavior verifies the ingredients before manufacturing.</p>
<p><strong>Raw tags:</strong></p>
<pre><code>{
"HyDustryUtility.Machine.Behaviour": ["Default"],
"HyDustryUtility.Machine.Requiere.Items": ["Ingredient_Crystal", "Ingredient_Dust"],
"HyDustryUtility.Machine.Requiere.Counts": ["2", "3"],
"HyDustryUtility.Machine.Output.Items": ["Refined_Crystal", "Byproduct_Dust"],
"HyDustryUtility.Machine.Output.Counts": ["1", "2"]
}</code></pre>
<p><strong>Component fields in Node Editor:</strong></p>
<pre><code>Type of operation: "Default"
Manufacturing time (1s -&gt; 1000mls): 5000
It requires energy: false
It requires fuel: false
InputItemsContainer: assign the input container
OutputItemsContainer: assign the output container</code></pre>
<p>The example requires two units of <code>Ingredient_Crystal</code> and three of <code>Ingredient_Dust</code> to produce one unit of <code>Refined_Crystal</code> and two of <code>Byproduct_Dust</code>. The IDs are examples; replace them with valid IDs from your mod.</p>
<h4><strong>2. Extractor with multiple resource types</strong></h4>
<p>The extractor scans positions relative to the block, identifies harvestable blocks whose drops are listed in <code>Scanner.Blocks</code>, counts the matches, and uses the most frequent resource. The index of each resource in <code>Scanner.Blocks</code> corresponds to its respective output tags, <code>Output.Items.N</code> and <code>Output.Counts.N</code>.</p>
<pre><code>{
"HyDustryUtility.Machine.Behaviour": ["Essence Extractor"],
"HyDustryUtility.Machine.Scanner.Positions": [
"0,0,-1",
"1,0,-1",
"-1,0,-1",
"0,0,-2"
],
"HyDustryUtility.Machine.Scanner.Blocks": [
"Ore_Copper",
"Ore_Iron"
],
"HyDustryUtility.Machine.Output.Items.0": ["Ingot_Copper"],
"HyDustryUtility.Machine.Output.Counts.0": ["2"],
"HyDustryUtility.Machine.Output.Items.1": ["Ingot_Iron"],
"HyDustryUtility.Machine.Output.Counts.1": ["1"],
"HyDustryUtility.Machine.Output.Items": ["Ore_Copper", "Ore_Iron"],
"HyDustryUtility.Machine.Output.Multiplicator": ["1.5", "2.0"]
}</code></pre>
<p>In this example, index 0 in <code>Scanner.Blocks</code> is <code>Ore_Copper</code>; therefore, its recipe uses <code>Output.Items.0</code>. Index 1 is <code>Ore_Iron</code>. If four copper blocks are detected, the system uses the primary product and the scanner's base quantity, combined with the configured multiplier. The final result may be rounded, as the extractor converts the calculated value into an integer.</p>
<p>If <code>Scanner.Positions</code> is not specified, the extractor attempts to use a single default position: <code>0,0,-1</code>.</p>
<p>Without <code>Scanner.Blocks</code>, there is no list of accepted resources to associate with the outputs.</p>
<p>&nbsp;</p>
<h4><strong>Optional Extractor Ingredients</strong></h4>
<p>To require the extractor to have specific materials before processing, include the tag <code>HyDustryUtility.Machine.Scanner.Require.Items</code>. Its value is not checked&mdash;the mere presence of the key is sufficient. The quantities and IDs are read from the key labeled <code>Requiere</code>.</p>
<pre><code>{
"HyDustryUtility.Machine.Scanner.Require.Items": ["true"],
"HyDustryUtility.Machine.Requiere.Items": ["Catalyst"],
"HyDustryUtility.Machine.Requiere.Counts": ["1"]
}</code></pre>
<p>In the extractor, if the <code>Scanner.Require.Items</code> flag is absent, the <code>Requiere</code> ingredients are neither checked nor consumed. In the <code>Default</code> behavior, however, ingredient verification is part of the production flow and does not depend on that flag.</p>
<h4><strong>3. Fuel and Upgrade Levels</strong></h4>
<p>To use fuel, enable <code>It requires fuel</code> in the component and configure the lists. The code searches for the fuel...the one that appears later in the list and for which a sufficient quantity is available. The upgrade level is determined by the fuel's position: the first fuel corresponds to level 1, the second to level 2, etc. The levels must be consecutive, starting from 1.</p>
<pre><code>{
"HyDustryUtility.Machine.Fuel.Items": ["Fuel_Basic", "Fuel_Advanced"],
"HyDustryUtility.Machine.Fuel.Counts": ["1", "2"],

"HyDustryUtility.Machine.Fuel.Upgrades.1.Items": ["Catalyst"],
"HyDustryUtility.Machine.Fuel.Upgrades.1.Counts": ["1"],
"HyDustryUtility.Machine.Fuel.Upgrades.1.ManufacturingTime": ["CraftTime"],
"HyDustryUtility.Machine.Fuel.Upgrades.1.TimeCounts": ["4000"],

"HyDustryUtility.Machine.Fuel.Upgrades.2.Items": ["Catalyst", "Shard"],
"HyDustryUtility.Machine.Fuel.Upgrades.2.Counts": ["2", "1"],
"HyDustryUtility.Machine.Fuel.Upgrades.2.ManufacturingTime": ["CraftTime"],
"HyDustryUtility.Machine.Fuel.Upgrades.2.TimeCounts": ["2000"],

"HyDustryUtility.Machine.Fuel.Scanner.Upgrades.1.Items": ["Ore_Copper"],
"HyDustryUtility.Machine.Fuel.Scanner.Upgrades.1.Multiplicator": ["2.5"]
}</code></pre>
<p>When an upgrade time needs to be read, the code takes the first numerical value from <code>TimeCounts</code>. The text <code>CraftTime</code> is a parallel key required by the parser, not an object ID consumed by the system. Regarding scanner upgrades, the multiplier for a given resource replaces the standard multiplier if active fuel is present and the corresponding upgrade is found.</p>
<p>The <code>Fuel.Upgrades.N.ManufacturingTime</code> tag is processed using the same pair-parsing logic employed for IDs and quantities. It requires at least one element in both arrays and an integer in <code>TimeCounts</code>; only the first resulting value is used as the duration. Please test this configuration within your target version of Hytale before distributing it.</p>
<h4><strong>Time multiplier based on scanned quantity</strong></h4>
<p>The extractor can look up a dynamic tag for each resource: <code>HyDustryUtility.Machine.Output.Multiplicator.Timer.&lt;ID&gt;</code>. The system attempts to select a duration based on the quantity of the predominant resource. If no valid value is found, it defaults to the component's <code>Manufacturing time</code>.</p>
<pre><code>{
"HyDustryUtility.Machine.Output.Multiplicator.Timer.Ore_Copper": [
"1000",
"2000",
"3000",
"4000",
"5000"
]
}</code></pre>
<p><strong>Important note on the current code:</strong> For a detected quantity N, the value at index N-1 is read&mdash;but only if the array contains more than N elements. Consequently, the current implementation requires an extra value: to support up to 4 blocks, the array must contain at least 5 values. This behavior is likely a boundary error in the code and should not be interpreted as an intentional requirement of the Node Editor format.</p>
<p>While <code>HyDustryUtility.Machine.Output.Timer.N</code> can be accessed via a helper utility, <code>Extractor.Execute</code> does not utilize that helper.</p>
<p>For the current extractor, the active key is <code>Output.Multiplicator.Timer.&lt;Resource ID&gt;</code>.</p>
<p>&nbsp;</p>
<h3><strong><span style="color: #3598db;"><em>ConveyorTransporterComponent</em></span></strong></h3>
<p>Add this component to the conveyor block so that the ticking systems can update its progress and search for or deposit items.</p>
<table>
<thead>
<tr>
<th>Component Field</th>
<th>Usage</th>
</tr>
</thead>
<tbody>
<tr>
<td><code>Velocity</code></td>
<td>The progress increment applied per tick while an item is moving. The code adds this value until it reaches 100; adjust and test the value based on the desired speed.</td>
</tr>
<tr>
<td><code>Progress (Only Tags)</code></td>
<td>The travel state, ranging from 0 to 100. This is updated by the system; it is not a raw tag with that specific name.</td>
</tr>
<tr>
<td><code>ItemsDetected (Only Tags)</code></td>
<td>A temporary indicator triggered by the item listener. The system reads it and then clears it.</td>
</tr>
<tr>
<td><code>Looking Behaviour (Only Tags)</code></td>
<td>A temporary state triggered by the tag <code>HyDustryUtility.Behaviour.Loocking</code>.</td>
</tr>
</tbody>
</table>
<p>Only <code>Velocity</code> is a recommended direct configuration option; the other three fields represent execution status.</p>
<h4><strong>Tags for a tape with route and loading/unloading</strong></h4>
<pre><code>{ 
"HyDustryUtility.Behaviour": ["true"], 
"HyDustryUtility.Behaviour.Loocking": ["true"], 
"HyDustryUtility.Paths": [ 
"0.5,0.5,0.0", 
"0.5,0.5,0.5", 
"0.5,0.5,1.0" 
], 
"HyDustryUtility.Items.Load.Positions": [ 
"0,0,-1", 
"1,0,0" 
], 
"HyDustryUtility.Items.Save.Positions": [ 
"0.0,1"
]
}</code></pre>
<p><code>Behaviour</code> allows the object to recognize the block as part of the path. <code>Looking</code> causes the conveyor to continue moving in search mode after a detection. The listener uses default loading positions&mdash;<code>0,0,-1</code>, <code>1,0,0</code>, and <code>-1,0,0</code>&mdash;if no loading tag exists; for unloading, it uses <code>0,0,1</code> if no unloading tag is present.</p>
<p>Loading/unloading offsets are interpreted relative to the conveyor's rotation and are used to locate neighboring blocks containing containers or machines. The <code>Paths</code> points define the object's position within the block; at least three distinct points must be defined to create a path that includes a start, a curve, and an end.</p>
<p><strong>Component fields in the Node Editor:</strong></p>
<pre><code>Velocity: 1.0
Progress (Only Tags): 0.0
ItemsDetected (Only Tags): false
Looking Behaviour (Only Tags): false</code></pre>
<p>Leave the progress at 0 and the indicators set to <code>false</code> when creating the conveyor belt. The system will update them during operation.</p>
<h3><strong><span style="color: #3598db;"><em>HarvestingEngineComponent</em></span></strong></h3>
<p>Add this component to the agricultural engine block. No <code>HyDustryUtility.Machine.*</code> tags are required to trigger its actions; these options are configured within the component itself.</p>
<table>
<thead>
<tr>
<th>Field</th>
<th>Type</th>
<th>Current Usage</th>
</tr>
</thead>
<tbody>
<tr>
<td><code>Range</code></td>
<td>long</td>
<td>Search radius applied across the X, Y, and Z axes; the area it is a cube, not a sphere.</td>
</tr>
<tr>
<td><code>Interval</code></td>
<td>long</td>
<td>Interval between actions, in milliseconds.</td>
</tr>
<tr>
<td><code>DecayWatering</code></td>
<td>int</td>
<td>Passed as the duration for watering; the current method calculates approximately 60 seconds per unit.</td>
</tr>
<tr>
<td><code>Water</code></td>
<td>boolean</td>
<td>Activates watering.</td>
</tr>
<tr>
<td><code>Fertilize</code></td>
<td>boolean</td>
<td>Activates fertilization.</td>
</tr>
<tr>
<td><code>Accelerate</code></td>
<td>boolean</td>
<td>Activates growth acceleration.</td>
</tr>
<tr>
<td><code>Harvest</code></td>
<td>boolean</td>
<td>Activates the search for and harvesting of blocks around the engine.</td>
</tr>
<tr>
<td><code>Fase</code></td>
<td>long</td>
<td>Incremented and reset by the listener, but the current processor does not use it to change actions.</td>
</tr>
<tr>
<td><code>DecayAccelerate</code></td>
<td>float</td>
<td>Stored, but does not affect the current acceleration calculation.</td>
</tr>
<tr>
<td><code>LastActionTime</code></td>
<td>string</td>
<td>Internal marker for scheduling the next action. Keep the initial value; it is updated by the system.</td>
</tr>
</tbody>
</table>
<p><strong>Example configuration: care engine</strong></p>
<pre><code>{
"Fase": 0,
"Range": 3,
"Interval": 5000,
"DecayWatering": 10,
"DecayAccelerate": 1.0,
"Water": true,
"Harvest": false,
"Fertilize": true,
"Accelerate": true,
"LastActionTime": "1970-01-01 00:00:00"
}</code></pre>
<p>With this example, the engine inspects a cube of 7x7x7 blocks; it attempts to perform an action every 5000 ms&mdash;watering, fertilizing, and accelerating crops&mdash;but it does not harvest. <code>DecayWatering</code> is converted into seconds using the formula already present in the code. <code>Fase</code> and <code>DecayAccelerate</code> are included because the component exposes them, even though they do not currently affect the processing outcome.</p>
<p><strong>Example configuration: harvester</strong></p>
<pre><code>{
"Fase": 0,
"Range": 2,
"Interval": 10000,
"DecayWatering": 0,
"DecayAccelerate": 1.0,
"Water": false,
"Harvest": true,
"Fertilize": false,
"Accelerate": false,
"LastActionTime": "1970-01-01 00:00:00"
}</code></pre>
<p>Harvesting places drops into the engine's local container when available. It verifies that the engine block has a compatible inventory and that the target blocks have harvest/collection data configured.</p>
<h3><strong><span style="color: #3598db;"><em>Vertical Tag and Neighboring Block Validators</em></span></strong></h3>
<p>Input/output validators, containers, and neighboring components check for the presence of <code>HyDustryUtility.Vertical</code>. If it is absent, they apply the expected horizontal offset after rotating the block. If it is present, they leave the X/Z coordinates unchanged and apply a vertical (Y) offset, with the sign determined by the rotation index.</p>
<pre><code>{
"HyDustryUtility.Vertical": ["true"]
}</code></pre>
<p>The tag does not specify the vertical distance:The code uses exactly one block&rsquo;s distance upward or downward. This behavior is specific to HyDustry Lib validators; it does not apply to all machines or the scanner.</p>
<h3><strong><span style="color: #3598db;"><em>Common Errors</em></span></strong></h3>
<ul>
<li><strong>Using the wrong prefix:</strong> writing <code>HyDustry.Behaviour</code> instead of <code>HyDustryUtility.Behaviour</code>.</li>
<li><strong>Using an incorrect ingredient key:</strong> the code expects <code>Machine.Requiere.Items</code>&mdash;not <code>Machine.Require.Items</code> or <code>Machine.Requires.Items</code>.</li>
<li><strong>Forgetting the component:</strong> tags describe positions and recipes, but they do not automatically add the <code>ConsumerMachineComponent</code>, <code>ConveyorTransporterComponent</code>, or <code>HarvestingEngineComponent</code>.</li>
<li><strong>Confusing quantities with JSON numbers:</strong> tag arrays are <code>String[]</code>; write <code>["2","5"]</code>, not <code>[2,5]</code>.</li>
<li><strong>Misaligning lists:</strong> ensure the index-based correspondence is maintained between <code>Items</code> and <code>Counts</code>, as well as between <code>Scanner.Blocks</code> and the recipe&rsquo;s <code>Output.Items.N</code> / <code>Output.Counts.N</code>.</li>
<li><strong>Using an incomplete path:</strong> movement is interpolated across the first three points; you must define a start, an intermediate point, and an end.</li>
<li><strong>Expecting functional energy:</strong> <code>It requires energy</code> is serialized, but it is not currently checked by the logic.</li>
<li><strong>Setting the wrong auxiliary timer:</strong> <code>Output.Timer.N</code> does not currently control <code>Extractor.Execute</code>; The key referenced by that operation is <code>Output.Multiplicator.Timer.ID</code>.</li>
</ul>
<h3><strong><span style="color: #3598db;"><em>Checklist before testing</em></span></strong></h3>
<ol>
<li>Confirm that the keys use the exact prefix <code>HyDustryUtility.</code>.</li>
<li>Add the corresponding component to the block via the Node Editor.</li>
<li>Configure the input/output containers within the machine component, rather than as text tags.</li>
<li>Ensure that each tag contains an array of strings and that the parallel lists maintain the same order.</li>
<li>Verify that the configured operation matches the registered name exactly&mdash;for example, <code>Essence Extractor</code>.</li>
<li>Begin testing with a small number of items, short distances, and a moderate interval; verify in-game that the offsets and rotations align with the block's orientation.</li>
</ol>