<%@page%>
<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<h2>
    <acme:print code="technician.dashboard.form.title.general-indicators"/>
</h2>

<table class="table table-sm">
    <tr>
        <th><acme:print code="technician.dashboard.form.label.number-maintenance-status.COMPLETED"/></th>
        <td><acme:print value="${numMaintenanceRecordsByStatus['COMPLETED']}"/></td>
    </tr>
    <tr>
        <th><acme:print code="technician.dashboard.form.label.number-maintenance-status.PENDING"/></th>
        <td><acme:print value="${numMaintenanceRecordsByStatus['PENDING']}"/></td>
    </tr>
    <tr>
        <th><acme:print code="technician.dashboard.form.label.number-maintenance-status.IN_PROGRESS"/></th>
        <td><acme:print value="${numMaintenanceRecordsByStatus['IN_PROGRESS']}"/></td>
    </tr>
    <tr>
        <th><acme:print code="technician.dashboard.form.label.nearest-next-inspection"/></th>
        <td><acme:print value="${nearestInspectionDue}"/></td>
    </tr>
    <tr>
        <th><acme:print code="technician.dashboard.form.label.most-tasks-aircrafts"/></th>
        <td>
            <jstl:forEach var="aircraft" items="${topAircraftByTasks}">
                <acme:print value="${aircraft}"/><br/>
            </jstl:forEach>
        </td>
    </tr>
    <tr>
        <th><acme:print code="technician.dashboard.form.label.cost-statistics.avg"/></th>
        <td><acme:print value="${costAverage}"/></td>
    </tr>
    <tr>
        <th><acme:print code="technician.dashboard.form.label.cost-statistics.min"/></th>
        <td><acme:print value="${costMinimum}"/></td>
    </tr>
    <tr>
        <th><acme:print code="technician.dashboard.form.label.cost-statistics.max"/></th>
        <td><acme:print value="${costMaximum}"/></td>
    </tr>
    <tr>
        <th><acme:print code="technician.dashboard.form.label.cost-statistics.dev"/></th>
        <td><acme:print value="${costDeviation}"/></td>
    </tr>
    <tr>
        <th><acme:print code="technician.dashboard.form.label.duration-statistics.avg"/></th>
        <td><acme:print value="${durationAverage}"/></td>
    </tr>
    <tr>
        <th><acme:print code="technician.dashboard.form.label.duration-statistics.min"/></th>
        <td><acme:print value="${durationMinimum}"/></td>
    </tr>
    <tr>
        <th><acme:print code="technician.dashboard.form.label.duration-statistics.max"/></th>
        <td><acme:print value="${durationMaximum}"/></td>
    </tr>
    <tr>
        <th><acme:print code="technician.dashboard.form.label.duration-statistics.dev"/></th>
        <td><acme:print value="${durationDeviation}"/></td>
    </tr>
</table>

<acme:return/>
