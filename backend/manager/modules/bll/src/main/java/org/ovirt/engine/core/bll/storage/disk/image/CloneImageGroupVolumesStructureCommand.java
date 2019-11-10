package org.ovirt.engine.core.bll.storage.disk.image;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;

import org.ovirt.engine.core.bll.CommandBase;
import org.ovirt.engine.core.bll.InternalCommandAttribute;
import org.ovirt.engine.core.bll.NonTransactiveCommandAttribute;
import org.ovirt.engine.core.bll.SerialChildCommandsExecutionCallback;
import org.ovirt.engine.core.bll.SerialChildExecutingCommand;
import org.ovirt.engine.core.bll.context.CommandContext;
import org.ovirt.engine.core.bll.tasks.interfaces.CommandCallback;
import org.ovirt.engine.core.bll.utils.CommandsWeightsUtils;
import org.ovirt.engine.core.bll.utils.PermissionSubject;
import org.ovirt.engine.core.common.action.ActionParametersBase.EndProcedure;
import org.ovirt.engine.core.common.action.ActionType;
import org.ovirt.engine.core.common.action.CloneImageGroupVolumesStructureCommandParameters;
import org.ovirt.engine.core.common.action.CreateVolumeContainerCommandParameters;
import org.ovirt.engine.core.common.businessentities.StorageDomainStatic;
import org.ovirt.engine.core.common.businessentities.storage.DiskImage;
import org.ovirt.engine.core.common.businessentities.storage.Image;
import org.ovirt.engine.core.common.businessentities.storage.VolumeFormat;
import org.ovirt.engine.core.common.businessentities.storage.VolumeType;
import org.ovirt.engine.core.compat.Guid;
import org.ovirt.engine.core.dao.DiskImageDao;
import org.ovirt.engine.core.dao.ImageDao;
import org.ovirt.engine.core.dao.StorageDomainStaticDao;

@InternalCommandAttribute
@NonTransactiveCommandAttribute
public class CloneImageGroupVolumesStructureCommand<T extends CloneImageGroupVolumesStructureCommandParameters> extends CommandBase<T> implements SerialChildExecutingCommand {

    @Inject
    private CommandsWeightsUtils commandsWeightsUtils;
    @Inject
    private DiskImageDao diskImageDao;
    @Inject
    private ImageDao imageDao;
    @Inject
    private StorageDomainStaticDao storageDomainStaticDao;
    @Inject
    @Typed(SerialChildCommandsExecutionCallback.class)
    private Instance<SerialChildCommandsExecutionCallback> callbackProvider;

    @Inject
    private ImagesHandler imagesHandler;

    public CloneImageGroupVolumesStructureCommand(T parameters, CommandContext cmdContext) {
        super(parameters, cmdContext);
        setStoragePoolId(getParameters().getStoragePoolId());
    }

    @Override
    protected void executeCommand() {
        List<DiskImage> images = diskImageDao.getAllSnapshotsForImageGroup(getParameters().getImageGroupID());
        ImagesHandler.sortImageList(images);
        getParameters().setImageIds(ImagesHandler.getDiskImageIds(images));
        prepareWeights();
        persistCommand(getParameters().getParentCommand(), getCallback() != null);
        setSucceeded(true);
    }

    private void prepareWeights() {
        if (getParameters().getJobWeight() == null) {
            return;
        }

        Double imageWeight = 1d / getParameters().getImageIds().size();
        List<Guid> imageIds = getParameters().getDestImages().isEmpty() ?
                getParameters().getImageIds() :
                getParameters().getDestImages()
                        .stream()
                        .map(d -> d.getImageId())
                        .collect(Collectors.toList());

        Map<String, Double> weightDivision =
                imageIds.stream().collect(Collectors.toMap(Guid::toString, z -> imageWeight));

        getParameters()
                .setOperationsJobWeight(commandsWeightsUtils.adjust(weightDivision, getParameters().getJobWeight()));
    }

    @Override
    public List<PermissionSubject> getPermissionCheckSubjects() {
        return Collections.emptyList();
    }

    @Override
    public CommandCallback getCallback() {
        return callbackProvider.get();
    }

    @Override
    public boolean performNextOperation(int completedChildren) {
        if (completedChildren == getParameters().getImageIds().size()) {
            return false;
        }

        Guid imageId = getParameters().getDestImages().isEmpty() ?
                getParameters().getImageIds().get(completedChildren) :
                getParameters().getDestImages().get(completedChildren).getImageId();
        log.info("Starting child command {} of {}, image '{}'",
                completedChildren + 1,
                getParameters().getImageIds().size(),
                imageId);

        if (!getParameters().getDestImages().isEmpty()) {
            createImage(getParameters().getDestImages().get(completedChildren), completedChildren);
        } else {
            createImage(diskImageDao.getSnapshotById(imageId), completedChildren);
        }
        return true;
    }

    private Guid determineSourceImageGroup(DiskImage image) {
        if (Guid.Empty.equals(image.getParentId())) {
            return Guid.Empty;
        } else if (image.getImageTemplateId().equals(image.getParentId())) {
            return imageDao.get(image.getImageTemplateId()).getDiskId();
        } else if (!getParameters().getDestImages().isEmpty()) {
            return getParameters().getDestImageGroupId();
        }

        return getParameters().getImageGroupID();

    }

    private void createImage(DiskImage image, int imageIndex) {
        VolumeFormat volumeFormat = determineVolumeFormat(getParameters().getDestDomain(),
                image.getVolumeFormat(),
                image.getVolumeType());

        Image innerImage = image.getImage();
        if (!getParameters().getDestImages().isEmpty()) {
            innerImage = diskImageDao.getSnapshotById(getParameters().getImageIds()
                    .get(imageIndex))
                    .getImage();
        }

        Long initialSize = imagesHandler.determineImageInitialSize(innerImage,
                volumeFormat,
                getParameters().getStoragePoolId(),
                getParameters().getSrcDomain(),
                getParameters().getDestDomain(),
                getParameters().getImageGroupID());

        CreateVolumeContainerCommandParameters parameters = new CreateVolumeContainerCommandParameters(
                getParameters().getStoragePoolId(),
                getParameters().getDestDomain(),
                determineSourceImageGroup(image),
                image.getParentId(),
                getParameters().getDestImageGroupId(),
                image.getImageId(),
                volumeFormat,
                image.getVolumeType(),
                getParameters().getDescription(),
                image.getSize(),
                initialSize);

        parameters.setEndProcedure(EndProcedure.COMMAND_MANAGED);
        parameters.setParentCommand(getActionType());
        parameters.setParentParameters(getParameters());
        parameters.setJobWeight(getParameters().getOperationsJobWeight().get(image.getImageId().toString()));
        runInternalActionWithTasksContext(ActionType.CreateVolumeContainer, parameters);
    }

    private VolumeFormat determineVolumeFormat(Guid destStorageDomainId,
            VolumeFormat srcVolumeFormat,
            VolumeType srcVolumeType) {
        // Block storage domain does not support raw/thin disk.
        // File based raw/thin disk will convert to cow/sparse
        if (srcVolumeFormat.equals(VolumeFormat.RAW) && srcVolumeType.equals(VolumeType.Sparse)) {
            StorageDomainStatic destStorageDomain = storageDomainStaticDao.get(destStorageDomainId);
            if (destStorageDomain.getStorageType().isBlockDomain()) {
                return VolumeFormat.COW;
            }
        }
        return srcVolumeFormat;
    }

    @Override
    public void handleFailure() {
    }
}
